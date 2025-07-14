package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para configurar la auditoría de requests y responses.
 * Prepara los datos necesarios para el logging de auditoría del sistema.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class AuditProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(AuditProcessor.class);

    /**
     * Configura los datos de auditoría en el exchange.
     * 
     * @param exchange el intercambio de Camel
     * @throws Exception si ocurre un error durante el procesamiento
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        configureRequestAudit(exchange);
        configureResponseMetadata(exchange);
        
        log.debug("Datos de auditoría configurados exitosamente");
    }

    /**
     * Configura la auditoría del request.
     */
    private void configureRequestAudit(Exchange exchange) {
        String requestBody = exchange.getIn().getBody(String.class);
        if (requestBody != null) {
            exchange.setProperty("requestAudit", requestBody);
        } else {
            exchange.setProperty("requestAudit", "");
        }
    }

    /**
     * Configura metadatos adicionales para la auditoría.
     */
    private void configureResponseMetadata(Exchange exchange) {
        // Configurar código y mensaje por defecto si no existen
        if (exchange.getProperty("Codigo") == null) {
            exchange.setProperty("Codigo", "200");
        }
        
        if (exchange.getProperty("Mensaje") == null) {
            exchange.setProperty("Mensaje", "Procesado exitosamente");
        }
    }
}

