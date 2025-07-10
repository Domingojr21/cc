package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for ConsultarDatosMaestroCedulados service.
 * Used for Cedula identification type queries.
 */
@RegisterForReflection
public record ConsultarDatosMaestroCeduladosRequest(
        @JsonProperty("clients") List<ClientMaestroRequestDto> clients,
        @JsonProperty("includeBinaryPhoto") boolean includeBinaryPhoto
) implements Serializable {
}
