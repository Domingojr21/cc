package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.ActualizarDatosMaestroCeduladosRequest;
import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.ClientActualizarRequestDto;
import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.IdentificationActualizarRequestDto;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ClientJCEResponseDto;
import com.banreservas.integration.model.outbound.response.backends.datosjcedp.ConsultarDatosJCEDPResponse;
import com.banreservas.integration.util.Constants;

import java.util.List;

/**
 * Processor to generate request for ActualizarDatosMaestroCedulados service.
 * Uses data from JCE service response to update master cedula data.
 */
@ApplicationScoped
public class GenerateService4RequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateService4RequestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Generando request para ActualizarDatosMaestroCedulados - Actualización datos maestros");

        ConsultarDatosJCEDPResponse jceResponse = 
            (ConsultarDatosJCEDPResponse) exchange.getProperty("jceResponse");

        if (jceResponse == null || jceResponse.body() == null || 
            jceResponse.body().clients() == null || jceResponse.body().clients().isEmpty()) {
            logger.error("Respuesta JCE no válida para generar request de actualización");
            throw new IllegalArgumentException("Respuesta JCE es requerida para actualizar datos maestros");
        }

        ClientJCEResponseDto jceClient = jceResponse.body().clients().get(0);
        
        // Crear identificación para actualización
        IdentificationActualizarRequestDto identification = new IdentificationActualizarRequestDto(
            jceClient.identifications().get(0).number(),
            jceClient.identifications().get(0).type()
        );

        // Crear cliente con datos de JCE
        ClientActualizarRequestDto client = new ClientActualizarRequestDto(
            List.of(identification),
            jceClient.names(),
            jceClient.firstSurname(),
            jceClient.secondSurname(),
            jceClient.birthDate(),
            jceClient.birthPlace(),
            jceClient.gender(),
            jceClient.maritalStatus(),
            jceClient.categoryId(),
            jceClient.category(),
            jceClient.cancelReasonId(),
            jceClient.cancelReason(),
            jceClient.stateId(),
            jceClient.state(),
            jceClient.cancelDate(),
            jceClient.nationCode(),
            jceClient.nationality(),
            "", // binaryPhoto vacío por defecto
            jceClient.expirationDate()
        );

        // Determinar si incluir foto binaria
        String includeBinaryPhoto = (String) exchange.getProperty("includeBinaryPhoto");
        boolean includeBinary = Constants.BOOLEAN_TRUE.equals(includeBinaryPhoto);

        ActualizarDatosMaestroCeduladosRequest service4Request = 
            new ActualizarDatosMaestroCeduladosRequest(List.of(client), includeBinary);

        exchange.getIn().setBody(service4Request);
        
        logger.info("Request generado exitosamente para actualización datos maestros - ID: {}, IncluirFoto: {}", 
                   jceClient.identifications().get(0).number(), includeBinary);
    }
}
