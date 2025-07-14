package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.outbound.backend.UpdateMasterCedulatedDataRequest;
import com.banreservas.integration.processors.GetDataOrchestrationDecisionProcessor;
import com.banreservas.integration.processors.ErrorResponseProcessorMICM;
import com.banreservas.integration.processors.GenerateGetDataBackendRequestsProcessor;
import com.banreservas.integration.processors.MapGetDataBackendResponseProcessor;
import com.banreservas.integration.processors.ValidateGetClientGeneralDataRequestProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.SocketTimeoutException;

/**
 * Ruta principal de orquestación para ConsultarDatosGeneralesCliente MICM.
 * Implementa las condiciones de orquestación para llamadas a servicios backend.
 * Maneja flujos dinámicos basados en respuestas de servicios.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class GetClientGeneralDataOrchestrationRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GetClientGeneralDataOrchestrationRoute.class);
    private static final Logger LOGGER_AUDIT = LoggerFactory.getLogger("ms-orq-consultar-datos-generales-cliente-micm");

    @Inject
    ValidateGetClientGeneralDataRequestProcessor validateRequestProcessor;

    @Inject
    GetDataOrchestrationDecisionProcessor orchestrationDecisionProcessor;

    @Inject
    GenerateGetDataBackendRequestsProcessor generateBackendRequestsProcessor;

    @Inject
    MapGetDataBackendResponseProcessor mapBackendResponseProcessor;

    @Inject
    ErrorResponseProcessorMICM errorResponseProcessor;

    @Override
    public void configure() throws Exception {

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
                .log(LoggingLevel.INFO, LOGGER_AUDIT,
			"sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | response=${body} | header= ${headers} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de timeout
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando a servicios backend")
                .setProperty("Mensaje", constant("Timeout al conectar con servicios backend"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                  .log(LoggingLevel.INFO, LOGGER_AUDIT,
			"sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | response=${body} | header= ${headers} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
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
                  .log(LoggingLevel.INFO, LOGGER_AUDIT,
			"sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | response=${body} | header= ${headers} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado en ConsultarDatosGeneralesCliente: ${exception.message}")
                .setProperty("Mensaje", simple("${exception.message}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process(errorResponseProcessor)
                .log(LoggingLevel.INFO, LOGGER_AUDIT,
			"sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | response=${body} | header= ${headers} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // RUTA PRINCIPAL MICM REST
        from("platform-http:/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente?httpMethodRestrict=POST")
                .routeId("consultar-datos-generales-cliente-micm-route")
                .log(LoggingLevel.INFO, logger, "Solicitud HTTP ConsultarDatosGeneralesCliente MICM recibida")
                
                // Log de headers del request

                // PASO 1: Validación del request
                .process(validateRequestProcessor)

                // PASO 2: Evaluación de condiciones de orquestación inicial
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

                // LLAMADA 3: ConsultarDatosJCE (inicial por forzarActualizar=TRUE o dinámico por código 904)
                .choice()
                    .when(exchangeProperty("callConsultarDatosJCE").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando flujo JCE secuencial")
                        .to("direct:execute-jce-flow")
                .end()

                // PASO 5: Verificar si se debe ejecutar JCE dinámicamente (código 904)
                .choice()
                    .when(exchangeProperty("clientNotFoundInMaster").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando flujo JCE dinámico por código 904")
                        .to("direct:execute-jce-flow")
                .end()

                // PASO 6: Mapeo de respuesta final
                .process(mapBackendResponseProcessor)

                // PASO 7: Preparar respuesta exitosa
                .process(exchange -> {
                    exchange.setProperty("Tipo", "0");
                    exchange.setProperty("Codigo", "200");
                    exchange.setProperty("Mensaje", "Consulta procesada exitosamente");
                })

                // PASO 8: Finalización
                .setHeader("sessionId", exchangeProperty("originalSessionId"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                      .log(LoggingLevel.INFO, LOGGER_AUDIT,
		"sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | response=${body} | header= ${headers} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.INFO, logger, "ConsultarDatosGeneralesCliente MICM procesado exitosamente");
                
        // RUTA SEPARADA PARA FLUJO JCE SECUENCIAL
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
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode jsonResponse = mapper.readTree(jceResponse);
                                
                                JsonNode responseBody = jsonResponse.path("body");
                                if (responseBody.has("clients") && responseBody.get("clients").isArray() && 
                                    responseBody.get("clients").size() > 0) {
                                    
                                    JsonNode client = responseBody.get("clients").get(0);
                                    
                                    // Extraer datos de identificación
                                    String identificationNumber = "";
                                    String identificationType = "";
                                    if (client.has("identifications") && client.get("identifications").isArray() && 
                                        client.get("identifications").size() > 0) {
                                        JsonNode identification = client.get("identifications").get(0);
                                        identificationNumber = identification.path("number").asText();
                                        identificationType = identification.path("type").asText();
                                    }
                                    
                                    String binaryPhoto = client.path("photoBinary").asText("");
                                    if (binaryPhoto == null || binaryPhoto.trim().isEmpty()) {
                                        binaryPhoto = "";
                                        logger.info("JCE no retornó foto binaria, usando string vacío para ActualizarMaestro");
                                    }
                                    
                                    UpdateMasterCedulatedDataRequest actualizarRequest = 
                                        UpdateMasterCedulatedDataRequest.fromJCEClientData(
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
                                            binaryPhoto, 
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
                        
                        // Ejecutar ActualizarDatosMaestro si tenemos datos JCE y foto binaria
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
     
                                            if (errorMsg != null && errorMsg.contains("binaryPhoto no puede estar vacío")) {
                                                logger.warn("ActualizarMaestro falló por binaryPhoto vacío - continuando con respuesta JCE");
                                                exchange.setProperty("hasBackendError", false);
                                                exchange.removeProperty("backendErrorMessage");
                                                exchange.removeProperty("backendErrorCode");
                                            } else {
                                                throw new RuntimeException(errorMsg != null ? errorMsg : "Error en ActualizarDatosMaestro");
                                            }
                                        })
                                    .otherwise()
                                        .setProperty("actualizarResponse", body())
                                        .log(LoggingLevel.INFO, logger, "ActualizarDatosMaestroCedulados ejecutado exitosamente")
                                .end()
                           .end()
                .end();
    }
}