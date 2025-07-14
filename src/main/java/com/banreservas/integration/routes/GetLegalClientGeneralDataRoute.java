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
 * Producer para el servicio ConsultarDatosGeneralesClienteJuridico.
 * Se ejecuta cuando el tipo de identificación es RNC.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class GetLegalClientGeneralDataRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(GetLegalClientGeneralDataRoute.class);
    
    @Override
    public void configure() throws Exception {
        
        from("direct:consultar-datos-juridico-service")
            .routeId("consultar-datos-juridico-service-route")
            .log(LoggingLevel.INFO, logger, "Iniciando ConsultarDatosGeneralesClienteJuridico")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            
            .choice()
                .when(simple("${exchangeProperty.originalSessionId} != null"))
                    .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
                .otherwise()
                    .setHeader("sessionId", constant("juridico123"))
            .end()
            
            .toD("{{consultar.datos.generales.cliente.juridico.url}}?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout={{timeout.consultar.datos.generales.cliente.juridico}}&connectionRequestTimeout={{timeout.consultar.datos.generales.cliente.juridico}}")
            
            .log(LoggingLevel.INFO, logger, "Código HTTP recibido: ${header.CamelHttpResponseCode}")
            .log(LoggingLevel.INFO, logger, "Respuesta recibida del backend: ${body}")
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "ConsultarDatosGeneralesClienteJuridico exitoso")
                .when(header("CamelHttpResponseCode").isNotEqualTo(200))
                    .log(LoggingLevel.ERROR, logger, "Error en ConsultarDatosGeneralesClienteJuridico - Código: ${header.CamelHttpResponseCode}")
                    .log(LoggingLevel.ERROR, logger, "Respuesta de error del backend: ${body}")
                    .process(exchange -> {
                        try {
                            String responseBody = exchange.getIn().getBody(String.class);
                            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
                            String errorMessage = "Error en el servicio backend juridico";
                            
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
                            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del backend juridico");
                            exchange.setProperty("hasBackendError", true);
                            exchange.setProperty("CamelExceptionCaught", new RuntimeException("Error procesando respuesta del backend juridico"));
                        }
                    })
            .end();
    }
}