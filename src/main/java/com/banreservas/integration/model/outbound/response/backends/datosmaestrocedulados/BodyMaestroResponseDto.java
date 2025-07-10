package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Response body containing client data from master cedula service.
 */
@RegisterForReflection
public record BodyMaestroResponseDto(
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("type") String type,
        @JsonProperty("clients") List<ClientMaestroResponseDto> clients
) implements Serializable {
}
