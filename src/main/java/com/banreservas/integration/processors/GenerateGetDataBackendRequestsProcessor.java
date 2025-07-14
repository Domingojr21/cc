package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.GetLegalClientGeneralDataRequest;
import com.banreservas.integration.model.outbound.backend.GetClientGeneralDataRequest;
import com.banreservas.integration.model.outbound.backend.GetJCEDataRequest;
import com.banreservas.integration.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para generar los requests JSON para los servicios backend de ConsultarDatosGeneralesCliente.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class GenerateGetDataBackendRequestsProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(GenerateGetDataBackendRequestsProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Generando requests para servicios backend ConsultarDatosGeneralesCliente");

        String identificacion = exchange.getProperty("identificacionRq", String.class);
        String tipoIdentificacion = exchange.getProperty("tipoIdentificacionRq", String.class);
        String incluirFotoBinaria = exchange.getProperty("incluirFotoBinariaRq", String.class);
        
        // Convertir incluirFotoBinaria a Boolean basado en el request
        Boolean includeBinaryPhoto = Constants.BOOLEAN_TRUE.equals(incluirFotoBinaria);
        
        // Generar request para ConsultarDatosGeneralesClienteJuridico (RNC)
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJuridico"))) {
            GetLegalClientGeneralDataRequest juridicoRequest = 
                GetLegalClientGeneralDataRequest.fromIdentification(
                    identificacion, tipoIdentificacion);
            exchange.setProperty("juridicoRequest", juridicoRequest);
        }
        
        // Generar request para ConsultarDatosMaestroCedulados (Cédula sin forzar)
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosMaestro"))) {
            GetClientGeneralDataRequest maestroRequest = 
                GetClientGeneralDataRequest.fromSingleIdentification(
                    identificacion, tipoIdentificacion, includeBinaryPhoto);
            exchange.setProperty("maestroRequest", maestroRequest);
        }
        
        // Generar request para ConsultarDatosJCE (Cédula con forzar o cuando maestro retorna 904)
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJCE"))) {
            GetJCEDataRequest jceRequest = 
                GetJCEDataRequest.fromSingleIdentification(
                    identificacion, tipoIdentificacion, includeBinaryPhoto);  
            exchange.setProperty("jceRequest", jceRequest);
        }
        
        log.info("Requests backend generados exitosamente");
    }
}