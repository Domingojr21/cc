package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.UpdateMasterCedulatedDataRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Ruta para el servicio de consulta de datos JCE.
 * Se ejecuta cuando el tipo de identificación es Cédula y se fuerza actualización,
 * o dinámicamente cuando el servicio de datos maestros retorna código 904.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class JCEDataServiceRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(JCEDataServiceRoute.class);
    @ConfigProperty(name = "consultar.datos.jcedp.url")
    String jceUrl;

    
    @ConfigProperty(name = "timeout.consultar.datos.jcedp") 
    String timeOut;

    @Override
    public void configure() throws Exception {
        
        from("direct:execute-jce-service")
            .routeId("jce-service-route")
            .log(LoggingLevel.INFO, logger, "Ejecutando servicio JCE")
            
            .setBody(exchangeProperty("jceRequest"))
            .marshal().json()
            .to("direct:call-jce-backend")
            
            .choice()
                .when(exchangeProperty("hasBackendError").isEqualTo(true))
                    .log(LoggingLevel.WARN, logger, "Error detectado en servicio JCE: ${exchangeProperty.backendErrorMessage}")
                .otherwise()
                    .setProperty("jceResponse", body())
                    .log(LoggingLevel.INFO, logger, "Servicio JCE completado exitosamente")
            .end();
            
        from("direct:call-jce-backend")
            .routeId("jce-backend-call-route")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
            
            .toD(jceUrl+"?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout="+timeOut+"&connectionRequestTimeout="+timeOut)
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio JCE")
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error en servicio JCE - Código: ${header.CamelHttpResponseCode}")
                    .process(this::processBackendError)
            .end();

        from("direct:execute-update-master-service-if-needed")
            .routeId("update-master-service-conditional-route")
            .log(LoggingLevel.INFO, logger, "Evaluando si se debe actualizar datos maestros")
            
            .process(this::extractJceDataForUpdate)
            
            .choice()
                .when(simple("${exchangeProperty.updateMasterRequest} != null"))
                    .log(LoggingLevel.INFO, logger, "Ejecutando actualización de datos maestros con datos JCE")
                    .to("direct:execute-update-master-service")
                .otherwise()
                    .log(LoggingLevel.INFO, logger, "No se requiere actualización de datos maestros")
            .end();
            
        from("direct:execute-update-master-service")
            .routeId("update-master-service-route")
            
            .setBody(exchangeProperty("updateMasterRequest"))
            .marshal().json()
            .to("direct:call-update-master-backend")
            
            .choice()
                .when(exchangeProperty("hasBackendError").isEqualTo(true))
                    .process(this::handleUpdateMasterError)
                .otherwise()
                    .setProperty("updateMasterResponse", body())
                    .log(LoggingLevel.INFO, logger, "Actualización de datos maestros completada exitosamente")
            .end();
            
        from("direct:call-update-master-backend")
            .routeId("update-master-backend-call-route")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
            
            .toD(jceUrl+"?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout="+timeOut+"&connectionRequestTimeout="+timeOut)
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio de actualización de datos maestros")
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error en servicio de actualización - Código: ${header.CamelHttpResponseCode}")
                    .process(this::processUpdateMasterBackendError)
            .end();
    }
    
    /**
     * Extrae los datos del cliente JCE para la actualización de datos maestros.
     */
    private void extractJceDataForUpdate(Exchange exchange) {
        try {
            String jceResponse = exchange.getProperty("jceResponse", String.class);
            if (jceResponse == null) {
                exchange.setProperty("updateMasterRequest", null);
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(jceResponse);
            
            JsonNode responseBody = jsonResponse.path("body");
            if (!responseBody.has("clients") || !responseBody.get("clients").isArray() || 
                responseBody.get("clients").size() == 0) {
                
                logger.warn("No se encontraron datos de cliente en la respuesta JCE");
                exchange.setProperty("updateMasterRequest", null);
                return;
            }
            
            JsonNode client = responseBody.get("clients").get(0);
            UpdateMasterCedulatedDataRequest updateRequest = buildUpdateRequest(client);
            
            exchange.setProperty("updateMasterRequest", updateRequest);
            logger.info("Datos JCE extraídos exitosamente para actualización");
            
        } catch (Exception e) {
            logger.error("Error extrayendo datos JCE: {}", e.getMessage());
            exchange.setProperty("updateMasterRequest", null);
        }
    }

    /**
     * Construye el request de actualización de datos maestros a partir de los datos JCE.
     */
    private UpdateMasterCedulatedDataRequest buildUpdateRequest(JsonNode client) {
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
            logger.info("JCE no retornó foto binaria, usando string vacío para actualización");
        }
        
        return UpdateMasterCedulatedDataRequest.fromJCEClientData(
            identificationNumber, identificationType,
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
    }

    /**
     * Maneja errores específicos de la actualización de datos maestros.
     */
    private void handleUpdateMasterError(Exchange exchange) {
        String errorMessage = exchange.getProperty("backendErrorMessage", String.class);
        
        // Si el error es por foto binaria vacía, continuar con respuesta JCE
        if (errorMessage != null && errorMessage.contains("binaryPhoto no puede estar vacío")) {
            logger.warn("Actualización falló por foto binaria vacía - continuando con respuesta JCE");
            exchange.setProperty("hasBackendError", false);
            exchange.removeProperty("backendErrorMessage");
            exchange.removeProperty("backendErrorCode");
        } else {
            throw new RuntimeException(errorMessage != null ? errorMessage : "Error en actualización de datos maestros");
        }
    }
    
    /**
     * Procesa errores del servicio JCE.
     */
    private void processBackendError(Exchange exchange) {
        processGenericBackendError(exchange, "Error en el servicio JCE");
    }

    /**
     * Procesa errores del servicio de actualización de datos maestros.
     */
    private void processUpdateMasterBackendError(Exchange exchange) {
        try {
            String responseBody = exchange.getIn().getBody(String.class);
            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
            String errorMessage = "Error en el servicio de actualización de datos maestros";
            
            if (responseBody != null && responseBody.contains("responseMessage")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonResponse = mapper.readTree(responseBody);
                
                String headerMessage = jsonResponse.path("header").path("responseMessage").asText();
                
                // Construir mensaje detallado incluyendo errores del body
                StringBuilder detailedMessage = new StringBuilder();
                detailedMessage.append("ActualizarDatosMaestroCedulados - ").append(headerMessage);
                
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
            
            logger.error("Error procesado - Código: {}, Mensaje: {}", httpCode, errorMessage);
            
        } catch (Exception e) {
            processGenericBackendError(exchange, "Error procesando respuesta del servicio de actualización");
        }
    }

    /**
     * Procesa errores genéricos de servicios backend.
     */
    private void processGenericBackendError(Exchange exchange, String defaultMessage) {
        try {
            String responseBody = exchange.getIn().getBody(String.class);
            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
            String errorMessage = defaultMessage;
            
            if (responseBody != null && responseBody.contains("responseMessage")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonResponse = mapper.readTree(responseBody);
                errorMessage = jsonResponse.path("header").path("responseMessage").asText(errorMessage);
            }
            
            exchange.setProperty("backendErrorMessage", errorMessage);
            exchange.setProperty("backendErrorCode", httpCode);
            exchange.setProperty("hasBackendError", true);
            
            logger.error("Error procesado - Código: {}, Mensaje: {}", httpCode, errorMessage);
            
        } catch (Exception e) {
            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
            exchange.setProperty("backendErrorCode", httpCode != null ? httpCode : 500);
            exchange.setProperty("backendErrorMessage", defaultMessage);
            exchange.setProperty("hasBackendError", true);
            
            logger.error("Error procesando respuesta del backend: {}", e.getMessage());
        }
    }
}