package com.banreservas.integration.model.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Main response DTO for ConsultarDatosGeneralesCliente service.
 * Contains header with response status and body with client data.
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteResponse(
        @JsonProperty("header") HeaderDto header,
        @JsonProperty("body") BodyDto body
) implements Serializable {
}