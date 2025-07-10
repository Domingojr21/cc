package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.ActualizarDatosMaestroCeduladosResponse;
import com.banreservas.integration.util.Constants;

/**
 * Processor to handle Service 4 response.
 * Processes update service response and prepares final response.
 */
@ApplicationScoped
public class ProcessService4ResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService4ResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de ActualizarDatosMaestroCedulados");

        ActualizarDatosMaestroCeduladosResponse service4Response = 
            exchange.getIn().getBody(ActualizarDatosMaestroCeduladosResponse.class);

        if (service4Response == null) {
            logger.error("Respuesta del servicio de actualización es nula");
            throw new IllegalArgumentException("Respuesta del servicio de actualización es requerida");
        }

        // Verificar respuesta exitosa
        if (service4Response.header().responseCode() != Constants.HTTP_OK) {
            logger.warn("Servicio de actualización retornó código de error: {} - {}", 
                       service4Response.header().responseCode(), 
                       service4Response.header().responseMessage());
            
            exchange.setProperty("service4Error", true);
            exchange.setProperty("service4ErrorCode", service4Response.header().responseCode());
            exchange.setProperty("service4ErrorMessage", service4Response.header().responseMessage());
            return;
        }

        // Verificar si la actualización fue exitosa
        if (service4Response.body() != null && 
            service4Response.body().clients() != null && 
            !service4Response.body().clients().isEmpty()) {
            
            logger.info("Datos maestros actualizados exitosamente");
            exchange.setProperty("dataUpdatedSuccessfully", true);
            exchange.setProperty("service4Response", service4Response);
            
        } else {
            logger.error("Respuesta del servicio de actualización sin datos de cliente");
            exchange.setProperty("service4Error", true);
            exchange.setProperty("service4ErrorCode", Constants.HTTP_INTERNAL_ERROR);
            exchange.setProperty("service4ErrorMessage", "Actualización sin datos de cliente");
        }
    }
}
