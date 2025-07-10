package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.request.ConsultarDatosGeneralesClienteRequest;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ClientJCERequestDto;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ClientsJCERequestDto;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ConsultarDatosJCEDPRequest;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.IdentificationJCERequestDto;
import com.banreservas.integration.util.Constants;

import java.util.List;

/**
 * Processor to generate request for ConsultarDatosJCEDP service.
 * Used when service 2 returns error 904 or force update is TRUE.
 */
@ApplicationScoped
public class GenerateService3RequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateService3RequestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para ConsultarDatosJCEDP - Consulta JCE");

        ConsultarDatosGeneralesClienteRequest mainRequest = 
            (ConsultarDatosGeneralesClienteRequest) exchange.getProperty("mainRequest");

        if (mainRequest == null) {
            logger.error("Request principal no encontrado en el exchange");
            throw new IllegalArgumentException("Request principal es requerido");
        }

        // Validar que sea tipo Cedula
        if (!Constants.IDENTIFICATION_TYPE_CEDULA.equals(mainRequest.identificationType())) {
            logger.error("Tipo de identificación inválido para servicio JCE: {}", 
                        mainRequest.identificationType());
            throw new IllegalArgumentException("Servicio JCE solo acepta tipo Cedula");
        }

        // Crear request para servicio 3
        IdentificationJCERequestDto identification = new IdentificationJCERequestDto(
            mainRequest.identification(),
            mainRequest.identificationType()
        );

        ClientJCERequestDto client = new ClientJCERequestDto(List.of(identification));
        ClientsJCERequestDto clients = new ClientsJCERequestDto(List.of(client));
        
        ConsultarDatosJCEDPRequest service3Request = new ConsultarDatosJCEDPRequest(clients);

        exchange.getIn().setBody(service3Request);
        
        logger.info("Request generado exitosamente para servicio JCE - ID: {}", 
                   mainRequest.identification());
    }
}