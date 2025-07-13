package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Producer para el servicio ConsultarDatosJCE.
 * Se ejecuta cuando el tipo de identificación es Cédula y se fuerza actualización.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class RestProducerConsultarDatosJCE extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(RestProducerConsultarDatosJCE.class);
    
    @Override
    public void configure() throws Exception {
        
        from("direct:consultar-datos-jce-service")
            .routeId("consultar-datos-jce-service-route")
            .log(LoggingLevel.INFO, logger, "Iniciando ConsultarDatosJCE")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            
            .choice()
                .when(simple("${exchangeProperty.originalSessionId} != null"))
                    .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
                .otherwise()
                    .setHeader("sessionId", constant("jce123"))
            .end()
            
            .toD("{{consultar.datos.jcedp.url}}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout={{timeout.consultar.datos.jcedp}}&connectionRequestTimeout={{timeout.consultar.datos.jcedp}}")
            
            .log(LoggingLevel.INFO, logger, "Código HTTP recibido: ${header.CamelHttpResponseCode}")
            .log(LoggingLevel.INFO, logger, "Respuesta recibida del backend: ${body}")
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "ConsultarDatosJCE exitoso")
                .when(header("CamelHttpResponseCode").isNotEqualTo(200))
                    .log(LoggingLevel.ERROR, logger, "Error en ConsultarDatosJCE - Código: ${header.CamelHttpResponseCode}")
                    .log(LoggingLevel.ERROR, logger, "Respuesta de error del backend: ${body}")
                    .process(exchange -> {
                        try {
                            String responseBody = exchange.getIn().getBody(String.class);
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            String errorMessage = "Error en el servicio backend JCE";
                            
                            if (responseBody != null && responseBody.contains("responseMessage")) {
                                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                                com.fasterxml.jackson.databind.JsonNode jsonResponse = mapper.readTree(responseBody);
                                errorMessage = jsonResponse.path("header").path("responseMessage").asText(errorMessage);
                            }
                            
                            exchange.setProperty("backendErrorMessage", errorMessage);
                            exchange.setProperty("backendErrorCode", httpCode);
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException(errorMessage));
                            
                            log.error("*** DEBUG: Error procesado - backendErrorCode={}, backendErrorMessage={}", httpCode, errorMessage);
                            
                        } catch (Exception e) {
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            exchange.setProperty("backendErrorCode", httpCode != null ? httpCode : "500");
                            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del backend JCE");
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException("Error procesando respuesta del backend JCE"));
                        }
                    })
            .end();
    }
}
