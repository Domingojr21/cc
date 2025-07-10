package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Request DTO for ConsultarDatosGeneralesClienteJuridico service.
 * Used for RNC identification type queries.
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteJuridicoRequest(
        @JsonProperty("client") ClientJuridicoRequestDto client
) implements Serializable {
}