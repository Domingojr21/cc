package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Identification request data for JCE service.
 */
@RegisterForReflection
public record IdentificationJCERequestDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type
) implements Serializable {
}