package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response DTO for ConsultarDatosMaestroCedulados service.
 */
@RegisterForReflection
public record ConsultarDatosMaestroCeduladosResponse(
        @JsonProperty("header") HeaderMaestroResponseDto header,
        @JsonProperty("body") BodyMaestroResponseDto body
) implements Serializable {
}

