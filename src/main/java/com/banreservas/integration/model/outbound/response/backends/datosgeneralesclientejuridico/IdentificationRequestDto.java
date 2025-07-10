package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Identification request data.
 */
@RegisterForReflection
public record IdentificationRequestDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type
) implements Serializable {
}
