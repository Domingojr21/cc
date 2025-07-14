package com.banreservas.integration.model.outbound;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Response genérico para respuestas de servicios backend.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 09/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record BackendResponse(
       @JsonProperty("header") Header header,
        @JsonProperty("body") Object body) implements Serializable {

    @RegisterForReflection
    public record Header(
            @JsonProperty("responseCode") int responseCode,
            @JsonProperty("responseMessage") String responseMessage) implements Serializable {
    }
}