package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.GetJCEDataRequest;
import com.banreservas.integration.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Producer para el servicio ConsultarDatosMaestroCedulados.
 * Se ejecuta cuando el tipo de identificación es Cédula y no se fuerza actualización.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class GetClientGeneralMasterDataRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(GetClientGeneralMasterDataRoute.class);
    
    @Override
    public void configure() throws Exception {
        
        from("direct:consultar-datos-maestro-service")
            .routeId("consultar-datos-maestro-service-route")
            .log(LoggingLevel.INFO, logger, "Iniciando ConsultarDatosMaestroCedulados")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            
            .choice()
                .when(simple("${exchangeProperty.originalSessionId} != null"))
                    .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
                .otherwise()
                    .setHeader("sessionId", constant("maestro123"))
            .end()
            
            .toD("{{consultar.datos.maestro.cedulados.url}}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout={{timeout.consultar.datos.maestro.cedulados}}&connectionRequestTimeout={{timeout.consultar.datos.maestro.cedulados}}")
            
            .log(LoggingLevel.INFO, logger, "Código HTTP recibido: ${header.CamelHttpResponseCode}")
            .log(LoggingLevel.INFO, logger, "Respuesta recibida del backend: ${body}")
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "ConsultarDatosMaestroCedulados exitoso")
                    .process(exchange -> {
                        try {
                            String responseBody = exchange.getIn().getBody(String.class);
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonResponse = mapper.readTree(responseBody);
                            String bodyCode = jsonResponse.path("body").path("code").asText();
                            
                            if (Constants.ERROR_CODE_NOT_FOUND.equals(bodyCode)) {
                                log.info("Cliente no encontrado en maestro (código 904) - Activar consulta JCE");
                                exchange.setProperty("clientNotFoundInMaster", true);
                                
                                // Activar dinámicamente los flujos JCE y ActualizarMaestro
                                exchange.setProperty("callConsultarDatosJCE", true);
                                exchange.setProperty("callActualizarDatosMaestro", true);
                                
                                // Generar request JCE dinámicamente
                                String identificacion = exchange.getProperty("identificacionRq", String.class);
                                String tipoIdentificacion = exchange.getProperty("tipoIdentificacionRq", String.class);
                                String incluirFotoBinaria = exchange.getProperty("incluirFotoBinariaRq", String.class);
                                Boolean includeBinaryPhoto = com.banreservas.integration.util.Constants.BOOLEAN_TRUE.equals(incluirFotoBinaria);
                                
                                GetJCEDataRequest jceRequest = 
                                    GetJCEDataRequest.fromSingleIdentification(
                                        identificacion, tipoIdentificacion, includeBinaryPhoto);
                                exchange.setProperty("jceRequest", jceRequest);
                                
                                log.info("Flujo JCE activado dinámicamente por código 904");
                             } else {
                                log.info("Cliente encontrado en maestro - código: {}", bodyCode);
                                exchange.setProperty("clientNotFoundInMaster", false);
                            }
                        } catch (Exception e) {
                            log.warn("Error verificando código 904: {}", e.getMessage());
                            exchange.setProperty("clientNotFoundInMaster", false);
                        }
                    })
                .when(header("CamelHttpResponseCode").isNotEqualTo(200))
                    .log(LoggingLevel.ERROR, logger, "Error en ConsultarDatosMaestroCedulados - Código: ${header.CamelHttpResponseCode}")
                    .log(LoggingLevel.ERROR, logger, "Respuesta de error del backend: ${body}")
                    .process(exchange -> {
                        try {
                            String responseBody = exchange.getIn().getBody(String.class);
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            String errorMessage = "Error en el servicio backend maestro";
                            
                            if (responseBody != null && responseBody.contains("responseMessage")) {
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode jsonResponse = mapper.readTree(responseBody);
                                errorMessage = jsonResponse.path("header").path("responseMessage").asText(errorMessage);
                            }
                            
                            exchange.setProperty("backendErrorMessage", errorMessage);
                            exchange.setProperty("backendErrorCode", httpCode);
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException(errorMessage));
                            
                            log.error("Error procesado - backendErrorCode={}, backendErrorMessage={}", httpCode, errorMessage);
                            
                        } catch (Exception e) {
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            exchange.setProperty("backendErrorCode", httpCode != null ? httpCode : "500");
                            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del backend maestro");
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException("Error procesando respuesta del backend maestro"));
                        }
                    })
            .end();
    }
}