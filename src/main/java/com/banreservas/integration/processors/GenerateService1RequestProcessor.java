package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.request.ConsultarDatosGeneralesClienteRequest;
import com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico.ClientJuridicoRequestDto;
import com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico.ConsultarDatosGeneralesClienteJuridicoRequest;
import com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico.IdentificationRequestDto;
import com.banreservas.integration.util.Constants;

/**
 * Processor to generate request for ConsultarDatosGeneralesClienteJuridico service.
 * Transforms main request to service 1 request format for RNC identification types.
 */
@ApplicationScoped
public class GenerateService1RequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateService1RequestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para ConsultarDatosGeneralesClienteJuridico - Tipo: RNC");

        ConsultarDatosGeneralesClienteRequest mainRequest = 
            (ConsultarDatosGeneralesClienteRequest) exchange.getProperty("mainRequest");

        if (mainRequest == null) {
            logger.error("Request principal no encontrado en el exchange");
            throw new IllegalArgumentException("Request principal es requerido");
        }

        // Validar que sea tipo RNC
        if (!Constants.IDENTIFICATION_TYPE_RNC.equals(mainRequest.identificationType())) {
            logger.error("Tipo de identificación inválido para servicio jurídico: {}", 
                        mainRequest.identificationType());
            throw new IllegalArgumentException("Servicio jurídico solo acepta tipo RNC");
        }

        // Crear request para servicio 1
        IdentificationRequestDto identification = new IdentificationRequestDto(
            mainRequest.identification(),
            mainRequest.identificationType()
        );

        ClientJuridicoRequestDto client = new ClientJuridicoRequestDto(identification);
        
        ConsultarDatosGeneralesClienteJuridicoRequest service1Request = 
            new ConsultarDatosGeneralesClienteJuridicoRequest(client);

        exchange.getIn().setBody(service1Request);
        
        logger.info("Request generado exitosamente para servicio jurídico - ID: {}", 
                   mainRequest.identification());
    }
}