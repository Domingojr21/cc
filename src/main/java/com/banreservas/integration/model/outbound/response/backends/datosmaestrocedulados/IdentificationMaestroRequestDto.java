package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Identification request data for master cedula service.
 */
@RegisterForReflection
public record IdentificationMaestroRequestDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type
) implements Serializable {
}
