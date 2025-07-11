package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.net.SocketTimeoutException;

/**
 * Ruta principal de orquestación para ConsultarDatosGeneralesCliente MICM.
 * Implementa las condiciones de orquestación para llamadas a servicios backend.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class ConsultarDatosGeneralesClienteRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarDatosGeneralesClienteRoute.class);

    @Inject
    Processor validateRequestProcessor;

    @Inject
    Processor orchestrationDecisionProcessor;

    @Inject
    Processor generateBackendRequestsProcessor;

    @Inject
    Processor mapBackendResponseProcessor;

    @Inject
    Processor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // ========================================
        // MANEJO GLOBAL DE EXCEPCIONES
        // ========================================
        
        // Manejo de errores de validación
        onException(ValidationException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Error de validación en ConsultarDatosGeneralesCliente: ${exception.message}")
                .process(exchange -> {
                    Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    exchange.setProperty("Mensaje", exception.getMessage());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                })
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de timeout
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando a servicios backend")
                .setProperty("Mensaje", constant("Timeout al conectar con servicios backend"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de excepciones generales
        onException(RuntimeException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "RuntimeException en ConsultarDatosGeneralesCliente: ${exception.message}")
                .choice()
                    .when(exchangeProperty("backendErrorCode").isNotNull())
                        .setProperty("Mensaje", simple("${exception.message}"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, exchangeProperty("backendErrorCode"))
                    .otherwise()
                        .setProperty("Mensaje", simple("${exception.message}"))
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .end()
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado en ConsultarDatosGeneralesCliente: ${exception.message}")
                .setProperty("Mensaje", simple("${exception.message}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // ========================================
        // RUTA PRINCIPAL MICM REST
        // ========================================
        from("servlet:consultar-datos-generales-cliente?httpMethodRestrict=POST")
                .routeId("consultar-datos-generales-cliente-micm-route")
                .log(LoggingLevel.INFO, logger, "Solicitud HTTP ConsultarDatosGeneralesCliente MICM recibida")
                
                // Log de headers del request
                .log(LoggingLevel.DEBUG, logger, "Headers recibidos - Canal: ${header.Canal}, Usuario: ${header.Usuario}, Servicio: ${header.Servicio}")

                // PASO 1: Validación del request
                .process(validateRequestProcessor)

                // PASO 2: Evaluación de condiciones de orquestación
                .process(orchestrationDecisionProcessor)

                // PASO 3: Generación de requests backend
                .process(generateBackendRequestsProcessor)

                // PASO 4: Ejecución condicional de servicios
                
                // LLAMADA 1: ConsultarDatosGeneralesClienteJuridico (RNC)
                .choice()
                    .when(exchangeProperty("callConsultarDatosJuridico").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando ConsultarDatosGeneralesClienteJuridico")
                        .setBody(exchangeProperty("juridicoRequest"))
                        .marshal().json(JsonLibrary.Jackson)
                        .to("direct:consultar-datos-juridico-service")
                        .choice()
                            .when(exchangeProperty("hasBackendError").isEqualTo(true))
                                .process(exchange -> {
                                    String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                                    throw new RuntimeException(errorMsg != null ? errorMsg : "Error en ConsultarDatosJuridico");
                                })
                            .otherwise()
                                .setProperty("juridicoResponse", body())
                                .log(LoggingLevel.INFO, logger, "ConsultarDatosJuridico completado exitosamente")
                        .end()
                .end()

                // LLAMADA 2: ConsultarDatosMaestroCedulados (Cédula sin forzar)
                .choice()
                    .when(exchangeProperty("callConsultarDatosMaestro").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando ConsultarDatosMaestroCedulados")
                        .setBody(exchangeProperty("maestroRequest"))
                        .marshal().json(JsonLibrary.Jackson)
                        .to("direct:consultar-datos-maestro-service")
                        .choice()
                            .when(exchangeProperty("hasBackendError").isEqualTo(true))
                                .process(exchange -> {
                                    String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                                    throw new RuntimeException(errorMsg != null ? errorMsg : "Error en ConsultarDatosMaestro");
                                })
                            .otherwise()
                                .setProperty("maestroResponse", body())
                                .log(LoggingLevel.INFO, logger, "ConsultarDatosMaestro completado exitosamente")
                        .end()
                .end()

                // LLAMADA 3 + 4: ConsultarDatosJCE + ActualizarDatosMaestro (Secuencial)
                .choice()
                    .when(exchangeProperty("callConsultarDatosJCE").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando flujo JCE secuencial")
                        .to("direct:execute-jce-flow")
                .end()

                // PASO 5: Mapeo de respuesta final
                .process(mapBackendResponseProcessor)

                // PASO 6: Preparar respuesta exitosa
                .process(exchange -> {
                    exchange.setProperty("Tipo", "0");
                    exchange.setProperty("Codigo", "200");
                    exchange.setProperty("Mensaje", "Consulta procesada exitosamente");
                })

                // PASO 7: Finalización
                .setHeader("sessionId", exchangeProperty("originalSessionId"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.INFO, logger, "ConsultarDatosGeneralesCliente MICM procesado exitosamente");

        // ========================================
        // RUTA SEPARADA PARA FLUJO JCE SECUENCIAL
        // ========================================
        from("direct:execute-jce-flow")
                .routeId("jce-flow-route")
                .log(LoggingLevel.INFO, logger, "Iniciando flujo JCE secuencial: ConsultarJCE -> ActualizarMaestro")
                
                // Ejecutar ConsultarDatosJCE
                .setBody(exchangeProperty("jceRequest"))
                .marshal().json(JsonLibrary.Jackson)
                .to("direct:consultar-datos-jce-service")
                
                .choice()
                    .when(exchangeProperty("hasBackendError").isEqualTo(true))
                        .process(exchange -> {
                            String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                            throw new RuntimeException(errorMsg != null ? errorMsg : "Error en ConsultarDatosJCE");
                        })
                    .otherwise()
                        .setProperty("jceResponse", body())
                        .log(LoggingLevel.INFO, logger, "ConsultarDatosJCE exitoso, extrayendo datos para actualización")
                        
                        // Extraer datos del cliente JCE para actualización
                        .process(exchange -> {
                            try {
                                String jceResponse = exchange.getProperty("jceResponse", String.class);
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonResponse = mapper.readTree(jceResponse);
                                
                                com.fasterxml.jackson.databind.JsonNode responseBody = jsonResponse.path("body");
                                if (responseBody.has("clients") && responseBody.get("clients").isArray() && 
                                    responseBody.get("clients").size() > 0) {
                                    
                                    com.fasterxml.jackson.databind.JsonNode client = responseBody.get("clients").get(0);
                                    
                                    // Extraer datos de identificación
                                    String identificationNumber = "";
                                    String identificationType = "";
                                    if (client.has("identifications") && client.get("identifications").isArray() && 
                                        client.get("identifications").size() > 0) {
                                        com.fasterxml.jackson.databind.JsonNode identification = client.get("identifications").get(0);
                                        identificationNumber = identification.path("number").asText();
                                        identificationType = identification.path("type").asText();
                                    }
                                    
                                    // Crear request para ActualizarDatosMaestro
                                    com.banreservas.integration.model.outbound.backend.ActualizarDatosMaestroCeduladosRequest actualizarRequest = 
                                        com.banreservas.integration.model.outbound.backend.ActualizarDatosMaestroCeduladosRequest.fromJCEClientData(
                                            identificationNumber,
                                            identificationType,
                                            client.path("names").asText(),
                                            client.path("firstSurname").asText(),
                                            client.path("secondSurname").asText(),
                                            client.path("birthDate").asText(),
                                            client.path("birthPlace").asText(),
                                            client.path("gender").asText(),
                                            client.path("maritalStatus").asText(),
                                            client.path("categoryId").asText(),
                                            client.path("category").asText(),
                                            client.path("cancelReasonId").asText(),
                                            client.path("cancelReason").asText(),
                                            client.path("stateId").asText(),
                                            client.path("state").asText(),
                                            client.path("cancelDate").asText(),
                                            client.path("nationCode").asText(),
                                            client.path("nationality").asText(),
                                            client.path("expirationDate").asText()
                                        );
                                    
                                    exchange.setProperty("actualizarRequestWithJCEData", actualizarRequest);
                                    logger.info("Datos JCE extraídos exitosamente para actualización");
                                    
                                } else {
                                    logger.warn("No se encontraron datos de cliente en la respuesta JCE");
                                    exchange.setProperty("actualizarRequestWithJCEData", null);
                                }
                                
                            } catch (Exception e) {
                                logger.error("Error extrayendo datos JCE: {}", e.getMessage());
                                exchange.setProperty("actualizarRequestWithJCEData", null);
                            }
                        })
                        
                        // Ejecutar ActualizarDatosMaestro si tenemos datos JCE
                        .choice()
                            .when(simple("${exchangeProperty.actualizarRequestWithJCEData} != null"))
                                .log(LoggingLevel.INFO, logger, "Ejecutando ActualizarDatosMaestroCedulados con datos JCE")
                                .setBody(exchangeProperty("actualizarRequestWithJCEData"))
                                .marshal().json(JsonLibrary.Jackson)
                                .to("direct:actualizar-datos-maestro-service")
                                .choice()
                                    .when(exchangeProperty("hasBackendError").isEqualTo(true))
                                        .process(exchange -> {
                                            String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                                            throw new RuntimeException(errorMsg != null ? errorMsg : "Error en ActualizarDatosMaestro");
                                        })
                                    .otherwise()
                                        .setProperty("actualizarResponse", body())
                                        .log(LoggingLevel.INFO, logger, "ActualizarDatosMaestroCedulados ejecutado exitosamente")
                                .end()
                                   .end()
                .end();
    }
}