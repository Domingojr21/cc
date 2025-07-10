package com.banreservas.integration.model.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response body containing client information.
 */
@RegisterForReflection
public record BodyDto(
        @JsonProperty("client") ClientDto client
) implements Serializable {
}