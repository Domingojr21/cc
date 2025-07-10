package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response DTO for ConsultarDatosGeneralesClienteJuridico service.
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteJuridicoResponse(
        @JsonProperty("header") HeaderResponseDto header,
        @JsonProperty("body") BodyJuridicoResponseDto body
) implements Serializable {
}