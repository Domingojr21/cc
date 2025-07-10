package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Request DTO for ConsultarDatosJCEDP service.
 * Used when master cedula service returns error 904 or force update is true.
 */
@RegisterForReflection
public record ConsultarDatosJCEDPRequest(
        @JsonProperty("clients") ClientsJCERequestDto clients
) implements Serializable {
}
