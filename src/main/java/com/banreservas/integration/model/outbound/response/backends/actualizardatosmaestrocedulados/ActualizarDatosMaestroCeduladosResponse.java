package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response DTO for ActualizarDatosMaestroCedulados service.
 */
@RegisterForReflection
public record ActualizarDatosMaestroCeduladosResponse(
        @JsonProperty("header") HeaderActualizarResponseDto header,
        @JsonProperty("body") BodyActualizarResponseDto body
) implements Serializable {
}