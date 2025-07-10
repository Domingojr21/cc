package com.banreservas.integration.model.inbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Main request DTO for ConsultarDatosGeneralesCliente service.
 * Contains client identification data and service options.
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteRequest(
        @JsonProperty("identificacion") String identification,
        @JsonProperty("tipoIdentificacion") String identificationType,
        @JsonProperty("forzarActualizar") String forceUpdate,
        @JsonProperty("incluirFotoBinaria") String includeBinaryPhoto
) implements Serializable {
}
