package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.ConsultarDatosGeneralesClienteJuridicoRequest;
import com.banreservas.integration.model.outbound.backend.ConsultarDatosGeneralesClienteRequest;
import com.banreservas.integration.model.outbound.backend.ConsultarDatosJCERequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para generar los requests JSON para los servicios backend de ConsultarDatosGeneralesCliente.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class GenerateConsultarDatosBackendRequestsProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(GenerateConsultarDatosBackendRequestsProcessor.class);
    
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Generando requests para servicios backend ConsultarDatosGeneralesCliente");

        String identificacion = exchange.getProperty("identificacionRq", String.class);
        String tipoIdentificacion = exchange.getProperty("tipoIdentificacionRq", String.class);
        String incluirFotoBinaria = exchange.getProperty("incluirFotoBinariaRq", String.class);
        
        // Convertir incluirFotoBinaria a Boolean
        Boolean includeBinaryPhoto = "TRUE".equals(incluirFotoBinaria);
        
        // Generar request para ConsultarDatosGeneralesClienteJuridico
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJuridico"))) {
            ConsultarDatosGeneralesClienteJuridicoRequest juridicoRequest = 
                ConsultarDatosGeneralesClienteJuridicoRequest.fromIdentification(identificacion, tipoIdentificacion);
            exchange.setProperty("juridicoRequest", juridicoRequest);
            log.debug("Request generado para ConsultarDatosGeneralesClienteJuridico");
        }
        
        // Generar request para ConsultarDatosMaestroCedulados
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosMaestro"))) {
            ConsultarDatosGeneralesClienteRequest maestroRequest = 
                ConsultarDatosGeneralesClienteRequest.fromSingleIdentification(
                    identificacion, tipoIdentificacion, includeBinaryPhoto);
            exchange.setProperty("maestroRequest", maestroRequest);
            log.debug("Request generado para ConsultarDatosMaestroCedulados");
        }
        
        // Generar request para ConsultarDatosJCE
        if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJCE"))) {
            ConsultarDatosJCERequest jceRequest = 
                ConsultarDatosJCERequest.fromSingleIdentification(identificacion, tipoIdentificacion);
            exchange.setProperty("jceRequest", jceRequest);
            log.debug("Request generado para ConsultarDatosJCE");
        }
        
        // NOTA: ActualizarDatosMaestroCeduladosRequest NO se genera aquí
        // Se genera dinámicamente en la ruta después de obtener los datos de ConsultarDatosJCE
        if (Boolean.TRUE.equals(exchange.getProperty("callActualizarDatosMaestro"))) {
            log.debug("ActualizarDatosMaestro será generado dinámicamente después de ConsultarDatosJCE");
        }
        
        log.info("Requests backend generados exitosamente");
    }
}