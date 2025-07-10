package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Request DTO for ActualizarDatosMaestroCedulados service.
 * Used to update master cedula data when force update is true.
 */
@RegisterForReflection
public record ActualizarDatosMaestroCeduladosRequest(
        @JsonProperty("clients") List<ClientActualizarRequestDto> clients,
        @JsonProperty("includeBinaryPhoto") boolean includeBinaryPhoto
) implements Serializable {
}
