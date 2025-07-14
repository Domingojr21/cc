package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Ruta para el servicio de consulta de datos generales de cliente jurídico.
 * Se ejecuta cuando el tipo de identificación es RNC.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class LegalClientGeneralDataRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(LegalClientGeneralDataRoute.class);
    
    @Override
    public void configure() throws Exception {
        
        from("direct:execute-legal-client-service")
            .routeId("legal-client-service-route")
            .log(LoggingLevel.INFO, logger, "Ejecutando servicio de cliente jurídico")
            
            .setBody(exchangeProperty("legalClientRequest"))
            .marshal().json()
            .to("direct:call-legal-client-backend")
            
            .choice()
                .when(exchangeProperty("hasBackendError").isEqualTo(true))
                    .log(LoggingLevel.WARN, logger, "Error detectado en servicio de cliente jurídico: ${exchangeProperty.backendErrorMessage}")
                .otherwise()
                    .setProperty("legalClientResponse", body())
                    .log(LoggingLevel.INFO, logger, "Servicio de cliente jurídico completado exitosamente")
            .end();
            
        from("direct:call-legal-client-backend")
            .routeId("legal-client-backend-call-route")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
            
            .toD("{{consultar.datos.generales.cliente.juridico.url}}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout={{timeout.consultar.datos.generales.cliente.juridico}}&connectionRequestTimeout={{timeout.consultar.datos.generales.cliente.juridico}}")
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio de cliente jurídico")
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error en servicio de cliente jurídico - Código: ${header.CamelHttpResponseCode}")
                    .process(this::processBackendError)
            .end();
    }
    
    /**
     * Procesa errores del servicio backend.
     */
    private void processBackendError(Exchange exchange) {
        try {
            String responseBody = exchange.getIn().getBody(String.class);
            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
            String errorMessage = "Error en el servicio de cliente jurídico";
            
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
            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del servicio de cliente jurídico");
            exchange.setProperty("hasBackendError", true);
            
            logger.error("Error procesando respuesta del backend: {}", e.getMessage());
        }
    }
}