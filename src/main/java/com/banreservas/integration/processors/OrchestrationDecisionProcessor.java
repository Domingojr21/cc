package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para evaluar las condiciones de orquestación de servicios backend.
 * Implementa la lógica de decisión para determinar qué servicios ejecutar según las reglas de negocio.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class OrchestrationDecisionProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(OrchestrationDecisionProcessor.class);
    
    /**
     * Evalúa las condiciones de orquestación para determinar qué servicios backend ejecutar.
     * 
     * @param exchange el intercambio de Camel que contiene los parámetros de decisión
     * @throws Exception si ocurre un error durante el procesamiento
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Evaluando condiciones de orquestación para consulta de datos generales de cliente");

        String identificationType = exchange.getProperty("identificationTypeRq", String.class);
        String forceUpdate = exchange.getProperty("forceUpdateRq", String.class);
        
        OrchestrationDecision decision = evaluateOrchestrationConditions(identificationType, forceUpdate);
        
        setOrchestrationFlags(exchange, decision);
        
        logOrchestrationDecision(identificationType, forceUpdate, decision);
        
        validateAtLeastOneServiceWillExecute(decision);
    }

    /**
     * Evalúa las condiciones de orquestación basadas en el tipo de identificación y forzar actualización.
     */
    private OrchestrationDecision evaluateOrchestrationConditions(String identificationType, String forceUpdate) {
        return new OrchestrationDecision(
            shouldCallLegalClientService(identificationType),
            shouldCallMasterDataService(identificationType, forceUpdate),
            shouldCallJceService(identificationType, forceUpdate),
            shouldCallUpdateMasterService(identificationType, forceUpdate)
        );
    }

    /**
     * Determina si se debe llamar al servicio de cliente jurídico.
     * Se ejecuta cuando el tipo de identificación es RNC.
     */
    private boolean shouldCallLegalClientService(String identificationType) {
        return Constants.IDENTIFICATION_TYPE_RNC.equals(identificationType);
    }

    /**
     * Determina si se debe llamar al servicio de datos maestros.
     * Se ejecuta cuando el tipo de identificación es Cédula y NO se fuerza actualización.
     */
    private boolean shouldCallMasterDataService(String identificationType, String forceUpdate) {
        return Constants.IDENTIFICATION_TYPE_CEDULA.equals(identificationType) && 
               Constants.BOOLEAN_FALSE.equals(forceUpdate);
    }

    /**
     * Determina si se debe llamar al servicio JCE.
     * Se ejecuta cuando el tipo de identificación es Cédula y SÍ se fuerza actualización.
     * También se puede activar dinámicamente si el servicio maestro retorna código 904.
     */
    private boolean shouldCallJceService(String identificationType, String forceUpdate) {
        return Constants.IDENTIFICATION_TYPE_CEDULA.equals(identificationType) && 
               Constants.BOOLEAN_TRUE.equals(forceUpdate);
    }

    /**
     * Determina si se debe llamar al servicio de actualización de datos maestros.
     * Se ejecuta DESPUÉS de ConsultarDatosJCE si es exitoso.
     */
    private boolean shouldCallUpdateMasterService(String identificationType, String forceUpdate) {
        return shouldCallJceService(identificationType, forceUpdate);
    }

    /**
     * Establece los flags de orquestación en el exchange.
     */
    private void setOrchestrationFlags(Exchange exchange, OrchestrationDecision decision) {
        exchange.setProperty("callLegalClientService", decision.callLegalClientService());
        exchange.setProperty("callMasterDataService", decision.callMasterDataService());
        exchange.setProperty("callJceService", decision.callJceService());
        exchange.setProperty("callUpdateMasterService", decision.callUpdateMasterService());
    }

    /**
     * Registra la decisión de orquestación en los logs.
     */
    private void logOrchestrationDecision(String identificationType, String forceUpdate, OrchestrationDecision decision) {
        log.info("Decisiones de orquestación - Tipo: {}, ForzarActualizar: {}", identificationType, forceUpdate);
        log.info("Llamadas programadas - Jurídico: {}, Maestro: {}, JCE: {}, ActualizarMaestro: {}", 
                decision.callLegalClientService(), decision.callMasterDataService(), 
                decision.callJceService(), decision.callUpdateMasterService());
        
        if (decision.callJceService() && decision.callUpdateMasterService()) {
            log.info("Flujo secuencial activado: ConsultarJCE -> ActualizarMaestro");
        }
    }

    /**
     * Valida que al menos un servicio será ejecutado.
     */
    private void validateAtLeastOneServiceWillExecute(OrchestrationDecision decision) {
        if (!decision.callLegalClientService() && !decision.callMasterDataService() && !decision.callJceService()) {
            log.warn("Ningún servicio será ejecutado para la configuración actual");
        }
    }

    /**
     * Record que encapsula la decisión de orquestación.
     */
    private record OrchestrationDecision(
        boolean callLegalClientService,
        boolean callMasterDataService,
        boolean callJceService,
        boolean callUpdateMasterService
    ) {}
}