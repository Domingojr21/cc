package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para evaluar las condiciones de orquestación para ConsultarDatosGeneralesCliente.
 * Implementa la lógica de decisión para llamar a los servicios backend apropiados.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ConsultarDatosOrchestrationDecisionProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ConsultarDatosOrchestrationDecisionProcessor.class);
    
    @ConfigProperty(name = "tipo.identificacion.cedula")
    String tipoIdentificacionCedula;
    
    @ConfigProperty(name = "tipo.identificacion.rnc")
    String tipoIdentificacionRnc;
    
    @ConfigProperty(name = "tipo.identificacion.pasaporte")
    String tipoIdentificacionPasaporte;
    
    @ConfigProperty(name = "forzar.actualizar.true")
    String forzarActualizarTrue;
    
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Evaluando condiciones de orquestación para ConsultarDatosGeneralesCliente");

        String tipoIdentificacion = exchange.getProperty("tipoIdentificacionRq", String.class);
        String forzarActualizar = exchange.getProperty("forzarActualizarRq", String.class);
        
        log.info("Configuración cargada - TipoCedula: {}, TipoRNC: {}, TipoPasaporte: {}, ForzarActualizar: {}", 
                tipoIdentificacionCedula, tipoIdentificacionRnc, tipoIdentificacionPasaporte, forzarActualizarTrue);
        
        // CONDICIÓN 1: ConsultarDatosGeneralesClienteJuridico
        // Se ejecuta cuando el tipo de identificación es RNC
        boolean callConsultarDatosJuridico = tipoIdentificacionRnc.equals(tipoIdentificacion);
        
        // CONDICIÓN 2: ConsultarDatosMaestroCedulados
        // Se ejecuta cuando el tipo de identificación es Cédula y NO se fuerza actualización
        boolean callConsultarDatosMaestro = tipoIdentificacionCedula.equals(tipoIdentificacion) && 
                                           !forzarActualizarTrue.equals(forzarActualizar);
        
        // CONDICIÓN 3: ConsultarDatosJCE
        // Se ejecuta cuando el tipo de identificación es Cédula y SÍ se fuerza actualización
        boolean callConsultarDatosJCE = tipoIdentificacionCedula.equals(tipoIdentificacion) && 
                                       forzarActualizarTrue.equals(forzarActualizar);
        
        // CONDICIÓN 4: ActualizarDatosMaestroCedulados
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