package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response header for master cedula service.
 */
@RegisterForReflection
public record HeaderMaestroResponseDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {
}
