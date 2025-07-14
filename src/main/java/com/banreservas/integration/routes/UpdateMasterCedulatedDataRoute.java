package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Producer para el servicio ActualizarDatosMaestroCedulados.
 * Se ejecuta después de ConsultarDatosJCE para actualizar los datos maestros.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class UpdateMasterCedulatedDataRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateMasterCedulatedDataRoute.class);
    @ConfigProperty(name = "actualizar.datos.maestro.cedulados.url")
    String updateMasterUrl;
    
    @ConfigProperty(name = "timeout.actualizar.datos.maestro.cedulados") 
    String timeOut;
    
    @Override
    public void configure() throws Exception {
        
        from("direct:actualizar-datos-maestro-service")
            .routeId("actualizar-datos-maestro-service-route")
            .log(LoggingLevel.INFO, logger, "Iniciando ActualizarDatosMaestroCedulados")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            
            .choice()
                .when(simple("${exchangeProperty.originalSessionId} != null"))
                    .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
                .otherwise()
                    .setHeader("sessionId", constant("actualizar123"))
            .end()
            
            .toD(updateMasterUrl+"?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout="+timeOut+"&connectionRequestTimeout="+timeOut)
            
            .log(LoggingLevel.INFO, logger, "Código HTTP recibido: ${header.CamelHttpResponseCode}")
            .log(LoggingLevel.INFO, logger, "Respuesta recibida del backend: ${body}")
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "ActualizarDatosMaestroCedulados exitoso")
                .when(header("CamelHttpResponseCode").isNotEqualTo(200))
                    .log(LoggingLevel.ERROR, logger, "Error en ActualizarDatosMaestroCedulados - Código: ${header.CamelHttpResponseCode}")
                    .log(LoggingLevel.ERROR, logger, "Respuesta de error del backend: ${body}")
                    .process(exchange -> {
                        try {
                            String responseBody = exchange.getIn().getBody(String.class);
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            String errorMessage = "Error en el servicio backend actualizar";
                            String serviceName = "ActualizarDatosMaestroCedulados";
                            
                            if (responseBody != null && responseBody.contains("responseMessage")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseBody);
            
            String headerMessage = jsonResponse.path("header").path("responseMessage").asText();
            
            // Extraer detalles del body si existen
            StringBuilder detailedMessage = new StringBuilder();
            detailedMessage.append(serviceName).append(" - ").append(headerMessage);
            
            JsonNode bodyNode = jsonResponse.path("body");
            if (bodyNode.isObject()) {
                bodyNode.fields().forEachRemaining(entry -> {
                    String fieldName = entry.getKey();
                    JsonNode fieldErrors = entry.getValue();
                    if (fieldErrors.isArray()) {
                        detailedMessage.append(". ").append(fieldName).append(": ");
                        for (JsonNode error : fieldErrors) {
                            detailedMessage.append(error.asText()).append(" ");
                        }
                    }
                });
            }
            
            errorMessage = detailedMessage.toString().trim();
        }
                            
                            exchange.setProperty("backendErrorMessage", errorMessage);
                            exchange.setProperty("backendErrorCode", httpCode);
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException(errorMessage));
                            
                            log.error("Error procesado - backendErrorCode={}, backendErrorMessage={}", httpCode, errorMessage);
                            
                        } catch (Exception e) {
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            exchange.setProperty("backendErrorCode", httpCode != null ? httpCode : "500");
                            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del backend actualizar");
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException("Error procesando respuesta del backend actualizar"));
                        }
                    })
            .end();
    }
}