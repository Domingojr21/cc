package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.GetJCEDataRequest;
import com.banreservas.integration.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Ruta para el servicio de consulta de datos maestros cedulados.
 * Se ejecuta cuando el tipo de identificación es Cédula y no se fuerza actualización.
 * Implementa lógica para activar dinámicamente el flujo JCE cuando el cliente no se encuentra (código 904).
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class MasterDataServiceRoute extends RouteBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterDataServiceRoute.class);
    @ConfigProperty(name = "consultar.datos.maestro.cedulados.url") 
    String masterDataUrl;

    @ConfigProperty(name = "timeout.consultar.datos.maestro.cedulados") 
    String timeOut;
    
    @Override
    public void configure() throws Exception {
        
        from("direct:execute-master-data-service")
            .routeId("master-data-service-route")
            .log(LoggingLevel.INFO, logger, "Ejecutando servicio de datos maestros")
            
            .setBody(exchangeProperty("masterDataRequest"))
            .marshal().json()
            .to("direct:call-master-data-backend")
            
            .choice()
                .when(exchangeProperty("hasBackendError").isEqualTo(true))
                    .log(LoggingLevel.WARN, logger, "Error detectado en servicio de datos maestros: ${exchangeProperty.backendErrorMessage}")
                .otherwise()
                    .setProperty("masterDataResponse", body())
                    .process(this::checkForClientNotFoundCode)
                    .log(LoggingLevel.INFO, logger, "Servicio de datos maestros completado exitosamente")
            .end();
            
        from("direct:call-master-data-backend")
            .routeId("master-data-backend-call-route")
            
            .removeHeaders("CamelHttp*")
            .removeHeader("host")
            
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader("sessionId", simple("${exchangeProperty.originalSessionId}"))
            
            .toD(masterDataUrl + "?bridgeEndpoint=true&throwExceptionOnFailure=false&connectTimeout="+timeOut+"&connectionRequestTimeout="+timeOut)
            
            .choice()
                .when(header("CamelHttpResponseCode").isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Respuesta exitosa del servicio de datos maestros")
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Error en servicio de datos maestros - Código: ${header.CamelHttpResponseCode}")
                    .process(this::processBackendError)
            .end();
    }
    
    /**
     * Verifica si el cliente no fue encontrado en datos maestros (código 904).
     * Si es así, activa dinámicamente el flujo JCE.
     */
    private void checkForClientNotFoundCode(Exchange exchange) {
        try {
            String responseBody = exchange.getIn().getBody(String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(responseBody);
            String bodyCode = jsonResponse.path("body").path("code").asText();
            
            if (Constants.ERROR_CODE_NOT_FOUND.equals(bodyCode)) {
                logger.info("Cliente no encontrado en datos maestros (código 904) - Activando consulta JCE");
                activateDynamicJceFlow(exchange);
            } else {
                logger.info("Cliente encontrado en datos maestros - código: {}", bodyCode);
                exchange.setProperty("clientNotFoundInMaster", false);
            }
        } catch (Exception e) {
            logger.warn("Error verificando código 904: {}", e.getMessage());
            exchange.setProperty("clientNotFoundInMaster", false);
        }
    }

    /**
     * Activa dinámicamente los flujos JCE y ActualizarMaestro.
     */
    private void activateDynamicJceFlow(Exchange exchange) {
        exchange.setProperty("clientNotFoundInMaster", true);
        exchange.setProperty("callJceService", true);
        exchange.setProperty("callUpdateMasterService", true);
        
        generateDynamicJceRequest(exchange);
        
        logger.info("Flujo JCE activado dinámicamente por código 904");
    }

    /**
     * Genera el request JCE dinámicamente con los parámetros originales.
     */
    private void generateDynamicJceRequest(Exchange exchange) {
        String identificationNumber = exchange.getProperty("identificationNumberRq", String.class);
        String identificationType = exchange.getProperty("identificationTypeRq", String.class);
        String includeBinaryPhoto = exchange.getProperty("includeBinaryPhotoRq", String.class);
        
        Boolean includeBinaryPhotoBoolean = Constants.BOOLEAN_TRUE.equals(includeBinaryPhoto);
        
        GetJCEDataRequest jceRequest = GetJCEDataRequest.fromSingleIdentification(
            identificationNumber, identificationType, includeBinaryPhotoBoolean);
        exchange.setProperty("jceRequest", jceRequest);
    }
    
    /**
     * Procesa errores del servicio backend.
     */
    private void processBackendError(Exchange exchange) {
        try {
            String responseBody = exchange.getIn().getBody(String.class);
            Integer httpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
            String errorMessage = "Error en el servicio de datos maestros";
            
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
            exchange.setProperty("backendErrorMessage", "Error procesando respuesta del servicio de datos maestros");
            exchange.setProperty("hasBackendError", true);
            
            logger.error("Error procesando respuesta del backend: {}", e.getMessage());
        }
    }
}