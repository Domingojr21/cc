package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response DTO for ConsultarDatosJCEDP service.
 */
@RegisterForReflection
public record ConsultarDatosJCEDPResponse(
        @JsonProperty("header") HeaderJCEResponseDto header,
        @JsonProperty("body") BodyJCEResponseDto body
) implements Serializable {
}
