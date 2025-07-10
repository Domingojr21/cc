package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response header for update service.
 */
@RegisterForReflection
public record HeaderActualizarResponseDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {
}
