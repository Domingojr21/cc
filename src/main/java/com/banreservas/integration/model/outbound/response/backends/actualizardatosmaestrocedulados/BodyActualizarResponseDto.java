package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Response body containing updated client data.
 */
@RegisterForReflection
public record BodyActualizarResponseDto(
        @JsonProperty("clients") List<ClientActualizarResponseDto> clients
) implements Serializable {
}
