package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.request.ConsultarDatosGeneralesClienteRequest;
import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.ClientMaestroRequestDto;
import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.ConsultarDatosMaestroCeduladosRequest;
import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.IdentificationMaestroRequestDto;
import com.banreservas.integration.util.Constants;

import java.util.List;

/**
 * Processor to generate request for ConsultarDatosMaestroCedulados service.
 * Transforms main request to service 2 request format for Cedula identification types.
 */
@ApplicationScoped
public class GenerateService2RequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateService2RequestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para ConsultarDatosMaestroCedulados - Tipo: Cedula");

        ConsultarDatosGeneralesClienteRequest mainRequest = 
            (ConsultarDatosGeneralesClienteRequest) exchange.getProperty("mainRequest");

        if (mainRequest == null) {
            logger.error("Request principal no encontrado en el exchange");
            throw new IllegalArgumentException("Request principal es requerido");
        }

        // Validar que sea tipo Cedula y forzarActualizar = FALSE
        if (!Constants.IDENTIFICATION_TYPE_CEDULA.equals(mainRequest.identificationType())) {
            logger.error("Tipo de identificación inválido para servicio maestro: {}", 
                        mainRequest.identificationType());
            throw new IllegalArgumentException("Servicio maestro solo acepta tipo Cedula");
        }

        if (!Constants.BOOLEAN_FALSE.equals(mainRequest.forceUpdate())) {
            logger.error("ForzarActualizar debe ser FALSE para servicio maestro: {}", 
                        mainRequest.forceUpdate());
            throw new IllegalArgumentException("Servicio maestro requiere forzarActualizar = FALSE");
        }

        // Crear request para servicio 2
        IdentificationMaestroRequestDto identification = new IdentificationMaestroRequestDto(
            mainRequest.identification(),
            mainRequest.identificationType()
        );

        ClientMaestroRequestDto client = new ClientMaestroRequestDto(List.of(identification));
        
        boolean includeBinaryPhoto = Constants.BOOLEAN_TRUE.equals(mainRequest.includeBinaryPhoto());
        
        ConsultarDatosMaestroCeduladosRequest service2Request = 
            new ConsultarDatosMaestroCeduladosRequest(List.of(client), includeBinaryPhoto);

        exchange.getIn().setBody(service2Request);
        
        logger.info("Request generado exitosamente para servicio maestro - ID: {}, IncluirFoto: {}", 
                   mainRequest.identification(), includeBinaryPhoto);
    }
}