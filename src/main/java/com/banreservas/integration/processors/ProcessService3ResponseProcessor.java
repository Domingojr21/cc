package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ConsultarDatosJCEDPResponse;
import com.banreservas.integration.util.Constants;

/**
 * Processor to handle Service 3 response and determine if data should be updated.
 * Processes JCE service response and decides if master data update is needed.
 */
@ApplicationScoped
public class ProcessService3ResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService3ResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de ConsultarDatosJCEDP");

        ConsultarDatosJCEDPResponse service3Response = 
            exchange.getIn().getBody(ConsultarDatosJCEDPResponse.class);

        if (service3Response == null) {
            logger.error("Respuesta del servicio JCE es nula");
            throw new IllegalArgumentException("Respuesta del servicio JCE es requerida");
        }

        // Verificar respuesta exitosa
        if (service3Response.header().responseCode() != Constants.HTTP_OK) {
            logger.warn("Servicio JCE retornó código de error: {} - {}", 
                       service3Response.header().responseCode(), 
                       service3Response.header().responseMessage());
            
            exchange.setProperty("service3Error", true);
            exchange.setProperty("service3ErrorCode", service3Response.header().responseCode());
            exchange.setProperty("service3ErrorMessage", service3Response.header().responseMessage());
            return;
        }

        // Verificar si se encontraron datos en JCE
        if (service3Response.body() != null && 
            service3Response.body().clients() != null && 
            !service3Response.body().clients().isEmpty()) {
            
            logger.info("Cliente encontrado en JCE - Proceder con actualización de datos maestros");
            exchange.setProperty("clientFoundInJCE", true);
            exchange.setProperty("jceResponse", service3Response);
            exchange.setProperty("callUpdateService", true);
            
        } else {
            logger.warn("Cliente no encontrado en JCE - No se puede actualizar datos maestros");
            exchange.setProperty("clientNotFoundInJCE", true);
            exchange.setProperty("service3Error", true);
            exchange.setProperty("service3ErrorCode", Constants.HTTP_BAD_REQUEST);
            exchange.setProperty("service3ErrorMessage", "Cliente no encontrado en JCE");
        }
    }
}