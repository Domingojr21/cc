package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.backend.GetLegalClientGeneralDataRequest;
import com.banreservas.integration.model.outbound.backend.GetClientGeneralDataRequest;
import com.banreservas.integration.model.outbound.backend.GetJCEDataRequest;
import com.banreservas.integration.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para generar los requests JSON para los servicios backend.
 * Crea los objetos de request específicos para cada servicio según la configuración de orquestación.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class BackendRequestGenerationProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(BackendRequestGenerationProcessor.class);

    /**
     * Genera los requests específicos para cada servicio backend según la configuración de orquestación.
     * 
     * @param exchange el intercambio de Camel que contiene los parámetros y flags de orquestación
     * @throws Exception si ocurre un error durante la generación
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Generando requests para servicios backend");

        BackendRequestParameters parameters = extractRequestParameters(exchange);
        
        generateLegalClientRequestIfNeeded(exchange, parameters);
        generateMasterDataRequestIfNeeded(exchange, parameters);
        generateJceRequestIfNeeded(exchange, parameters);
        
        log.info("Requests backend generados exitosamente");
    }

    /**
     * Extrae los parámetros necesarios del exchange para generar los requests.
     */
    private BackendRequestParameters extractRequestParameters(Exchange exchange) {
        String identificationNumber = exchange.getProperty("identificationNumberRq", String.class);
        String identificationType = exchange.getProperty("identificationTypeRq", String.class);
        String includeBinaryPhoto = exchange.getProperty("includeBinaryPhotoRq", String.class);
        
        Boolean includeBinaryPhotoBoolean = Constants.BOOLEAN_TRUE.equals(includeBinaryPhoto);
        
        return new BackendRequestParameters(identificationNumber, identificationType, includeBinaryPhotoBoolean);
    }

    /**
     * Genera el request para el servicio de cliente jurídico si es necesario.
     */
    private void generateLegalClientRequestIfNeeded(Exchange exchange, BackendRequestParameters parameters) {
        if (Boolean.TRUE.equals(exchange.getProperty("callLegalClientService"))) {
            GetLegalClientGeneralDataRequest legalRequest = 
                GetLegalClientGeneralDataRequest.fromIdentification(
                    parameters.identificationNumber(), parameters.identificationType());
            exchange.setProperty("legalClientRequest", legalRequest);
        }
    }

    /**
     * Genera el request para el servicio de datos maestros si es necesario.
     */
    private void generateMasterDataRequestIfNeeded(Exchange exchange, BackendRequestParameters parameters) {
        if (Boolean.TRUE.equals(exchange.getProperty("callMasterDataService"))) {
            GetClientGeneralDataRequest masterRequest = 
                GetClientGeneralDataRequest.fromSingleIdentification(
                    parameters.identificationNumber(), 
                    parameters.identificationType(), 
                    parameters.includeBinaryPhoto());
            exchange.setProperty("masterDataRequest", masterRequest);
        }
    }

    /**
     * Genera el request para el servicio JCE si es necesario.
     */
    private void generateJceRequestIfNeeded(Exchange exchange, BackendRequestParameters parameters) {
        if (Boolean.TRUE.equals(exchange.getProperty("callJceService"))) {
            GetJCEDataRequest jceRequest = 
                GetJCEDataRequest.fromSingleIdentification(
                    parameters.identificationNumber(), 
                    parameters.identificationType(), 
                    parameters.includeBinaryPhoto());  
            exchange.setProperty("jceRequest", jceRequest);
        }
    }

    /**
     * Record que encapsula los parámetros necesarios para generar requests backend.
     */
    private record BackendRequestParameters(
        String identificationNumber,
        String identificationType,
        Boolean includeBinaryPhoto
    ) {}
}