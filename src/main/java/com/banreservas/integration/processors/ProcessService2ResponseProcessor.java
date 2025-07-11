package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.ConsultarDatosMaestroCeduladosResponse;
import com.banreservas.integration.util.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Processor to handle Service 2 response and determine next steps.
 * Checks if client was found or if JCE service should be called.
 */
@ApplicationScoped
public class ProcessService2ResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService2ResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de ConsultarDatosMaestroCedulados");

        String responseBody = exchange.getIn().getBody(String.class);
        logger.info("Response body as String: {}", responseBody);

        if (responseBody == null || responseBody.isEmpty()) {
            logger.error("Respuesta del servicio maestro está vacía");
            exchange.setProperty("service2Error", true);
            exchange.setProperty("service2ErrorCode", Constants.HTTP_INTERNAL_ERROR);
            exchange.setProperty("service2ErrorMessage", "Respuesta del servicio maestro está vacía");
            return;
        }

        ConsultarDatosMaestroCeduladosResponse service2Response = null;
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            service2Response = mapper.readValue(responseBody, ConsultarDatosMaestroCeduladosResponse.class);
            
            logger.info("Response deserialized successfully - Header code: {}, Body code: {}", 
                       service2Response.header().responseCode(),
                       service2Response.body() != null ? service2Response.body().code() : "NULL");
                       
        } catch (Exception e) {
            logger.error("Error deserializing service2 response: {}", e.getMessage());
            exchange.setProperty("service2Error", true);
            exchange.setProperty("service2ErrorCode", Constants.HTTP_INTERNAL_ERROR);
            exchange.setProperty("service2ErrorMessage", "Error deserializing master cedula service response");
            return;
        }

        if (service2Response == null) {
            logger.error("Respuesta del servicio maestro es nula después de deserialización");
            exchange.setProperty("service2Error", true);
            exchange.setProperty("service2ErrorCode", Constants.HTTP_INTERNAL_ERROR);
            exchange.setProperty("service2ErrorMessage", "Respuesta del servicio maestro es nula");
            return;
        }

        // Verificar respuesta exitosa del header
        if (service2Response.header().responseCode() != Constants.HTTP_OK) {
            logger.warn("Servicio maestro retornó código de error en header: {} - {}", 
                       service2Response.header().responseCode(), 
                       service2Response.header().responseMessage());
            
            exchange.setProperty("service2Error", true);
            exchange.setProperty("service2ErrorCode", service2Response.header().responseCode());
            exchange.setProperty("service2ErrorMessage", service2Response.header().responseMessage());
            return;
        }

        // Verificar el código del body
        if (service2Response.body() != null && 
            Constants.ERROR_CODE_NOT_FOUND.equals(service2Response.body().code())) {
            
            logger.info("Cliente no encontrado en datos maestros (código 904) - Proceder con consulta JCE");
            exchange.setProperty("clientNotFoundInMaster", true);
            exchange.setProperty("callJCEService", true);
            
        } else if (service2Response.body() != null && 
                   Constants.ERROR_CODE_SUCCESS.equals(service2Response.body().code())) {
            
            logger.info("Cliente encontrado en datos maestros - Respuesta exitosa");
            exchange.setProperty("clientFoundInMaster", true);
            // IMPORTANTE: Almacenar la respuesta completa en las propiedades del exchange
            exchange.setProperty("service2Response", service2Response);
            
            logger.info("Stored service2Response in exchange properties - Clients count: {}", 
                       service2Response.body().clients() != null ? service2Response.body().clients().size() : "NULL");
            
            // Verificar si se debe forzar actualización
            String forceUpdate = (String) exchange.getProperty("forceUpdate");
            if (Constants.BOOLEAN_TRUE.equals(forceUpdate)) {
                logger.info("ForzarActualizar = TRUE - Proceder con consulta JCE para actualizar");
                exchange.setProperty("callJCEService", true);
            }
            
        } else {
            logger.error("Respuesta del servicio maestro con código inesperado: {}", 
                        service2Response.body() != null ? service2Response.body().code() : "null");
            exchange.setProperty("service2Error", true);
            exchange.setProperty("service2ErrorCode", Constants.HTTP_INTERNAL_ERROR);
            exchange.setProperty("service2ErrorMessage", "Código de respuesta inesperado del servicio maestro");
        }
    }
}