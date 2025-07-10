package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.BodyDto;
import com.banreservas.integration.model.outbound.response.ClientDto;
import com.banreservas.integration.model.outbound.response.ConsultarDatosGeneralesClienteResponse;
import com.banreservas.integration.model.outbound.response.HeaderDto;
import com.banreservas.integration.model.outbound.response.IdentificationDto;
import com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico.ConsultarDatosGeneralesClienteJuridicoResponse;
import com.banreservas.integration.util.Constants;

/**
 * Processor to handle Service 1 response and generate final response.
 * Maps juridical client data to main response format.
 */
@ApplicationScoped
public class ProcessService1ResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessService1ResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de ConsultarDatosGeneralesClienteJuridico");

        ConsultarDatosGeneralesClienteJuridicoResponse service1Response = 
            exchange.getIn().getBody(ConsultarDatosGeneralesClienteJuridicoResponse.class);

        if (service1Response == null) {
            logger.error("Respuesta del servicio jurídico es nula");
            throw new IllegalArgumentException("Respuesta del servicio jurídico es requerida");
        }

        // Verificar respuesta exitosa
        if (service1Response.header().responseCode() != Constants.HTTP_OK) {
            logger.warn("Servicio jurídico retornó código de error: {} - {}", 
                       service1Response.header().responseCode(), 
                       service1Response.header().responseMessage());
            
            // Generar respuesta con el error del servicio
            HeaderDto errorHeader = new HeaderDto(
                service1Response.header().responseCode(),
                service1Response.header().responseMessage()
            );
            
            ConsultarDatosGeneralesClienteResponse errorResponse = 
                new ConsultarDatosGeneralesClienteResponse(errorHeader, null);
            
            exchange.getIn().setBody(errorResponse);
            return;
        }

        // Mapear respuesta exitosa
        if (service1Response.body() != null && service1Response.body().client() != null) {
            var juridicoClient = service1Response.body().client();
            
            IdentificationDto identification = new IdentificationDto(
                juridicoClient.identification().number(),
                juridicoClient.identification().type()
            );
            
            ClientDto client = new ClientDto(
                identification,
                juridicoClient.businessName(),
                juridicoClient.tradeName()
            );
            
            BodyDto body = new BodyDto(client);
            HeaderDto header = new HeaderDto(Constants.HTTP_OK, Constants.RESPONSE_MESSAGE_SUCCESS);
            
            ConsultarDatosGeneralesClienteResponse finalResponse = 
                new ConsultarDatosGeneralesClienteResponse(header, body);
            
            exchange.getIn().setBody(finalResponse);
            
            logger.info("Respuesta exitosa procesada para cliente jurídico - RNC: {}", 
                       juridicoClient.identification().number());
        } else {
            logger.error("Respuesta del servicio jurídico sin datos de cliente");
            throw new IllegalArgumentException("Respuesta del servicio jurídico sin datos de cliente");
        }
    }
}