package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.util.Constants;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para evaluar las condiciones de orquestación para ConsultarDatosGeneralesCliente.
 * Implementa la lógica de decisión para llamar a los servicios backend apropiados según las reglas de negocio.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class GetDataOrchestrationDecisionProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(GetDataOrchestrationDecisionProcessor.class);
    
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Evaluando condiciones de orquestación para ConsultarDatosGeneralesCliente");

        String tipoIdentificacion = exchange.getProperty("tipoIdentificacionRq", String.class);
        String forzarActualizar = exchange.getProperty("forzarActualizarRq", String.class);
        
        // FLUJO 1: ConsultarDatosGeneralesClienteJuridico
        // Se ejecuta cuando el tipo de identificación es RNC
        boolean callConsultarDatosJuridico = Constants.IDENTIFICATION_TYPE_RNC.equals(tipoIdentificacion);
        
        // FLUJO 2: ConsultarDatosMaestroCedulados
        // Se ejecuta cuando el tipo de identificación es Cédula y NO se fuerza actualización
        boolean callConsultarDatosMaestro = Constants.IDENTIFICATION_TYPE_CEDULA.equals(tipoIdentificacion) && 
                                           Constants.BOOLEAN_FALSE.equals(forzarActualizar);
        
        // FLUJO 3: ConsultarDatosJCE
        // Se ejecuta cuando el tipo de identificación es Cédula y SÍ se fuerza actualización
        // NOTA: También se puede activar dinámicamente si ConsultarDatosMaestro retorna código 904
        boolean callConsultarDatosJCE = Constants.IDENTIFICATION_TYPE_CEDULA.equals(tipoIdentificacion) && 
                                       Constants.BOOLEAN_TRUE.equals(forzarActualizar);
        
        // FLUJO 4: ActualizarDatosMaestroCedulados
        // Se ejecuta DESPUÉS de ConsultarDatosJCE si es exitoso
        boolean callActualizarDatosMaestro = callConsultarDatosJCE;
        
        // Configurar flags en el exchange
        exchange.setProperty("callConsultarDatosJuridico", callConsultarDatosJuridico);
        exchange.setProperty("callConsultarDatosMaestro", callConsultarDatosMaestro);
        exchange.setProperty("callConsultarDatosJCE", callConsultarDatosJCE);
        exchange.setProperty("callActualizarDatosMaestro", callActualizarDatosMaestro);
        
        log.info("Decisiones de orquestación - Tipo: {}, ForzarActualizar: {}", tipoIdentificacion, forzarActualizar);
        log.info("Llamadas programadas - Juridico: {}, Maestro: {}, JCE: {}, ActualizarMaestro: {}", 
                callConsultarDatosJuridico, callConsultarDatosMaestro, callConsultarDatosJCE, callActualizarDatosMaestro);
        
        if (callConsultarDatosJCE && callActualizarDatosMaestro) {
            log.info("Flujo secuencial activado: ConsultarJCE -> ActualizarMaestro");
        }
        
        // Validar que al menos un servicio será llamado
        if (!callConsultarDatosJuridico && !callConsultarDatosMaestro && !callConsultarDatosJCE) {
            log.warn("Ningún servicio será ejecutado para tipo de identificación: {}", tipoIdentificacion);
        }
    }
}