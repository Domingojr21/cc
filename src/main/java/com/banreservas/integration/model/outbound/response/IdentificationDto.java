package com.banreservas.integration.model.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Client identification information.
 */
@RegisterForReflection
public record IdentificationDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type
) implements Serializable {
}