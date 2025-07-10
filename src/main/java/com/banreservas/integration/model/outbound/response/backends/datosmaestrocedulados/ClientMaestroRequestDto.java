package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Client request data for master cedula queries.
 */
@RegisterForReflection
public record ClientMaestroRequestDto(
        @JsonProperty("identifications") List<IdentificationMaestroRequestDto> identifications
) implements Serializable {
}
