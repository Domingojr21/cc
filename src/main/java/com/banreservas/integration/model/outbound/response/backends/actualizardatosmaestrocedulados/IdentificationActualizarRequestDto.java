package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public record IdentificationActualizarRequestDto(
        @JsonProperty("number") String number,
        @JsonProperty("type") String type
) implements Serializable {
}
