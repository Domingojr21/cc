package com.banreservas.integration.model.outbound;

import java.io.Serializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Response genérico para respuestas de servicios backend.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record BackendResponse(
        Header header,
        Object body) implements Serializable {

    @RegisterForReflection
    public record Header(
            int responseCode,
            String responseMessage) implements Serializable {
    }
}