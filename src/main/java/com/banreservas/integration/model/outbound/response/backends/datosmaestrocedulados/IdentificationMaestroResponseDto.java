package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Identification response data with expiration date.
 */
@RegisterForReflection
public record IdentificationMaestroResponseDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type,
        @JsonProperty("expirationDate") String expirationDate
) implements Serializable {
}
