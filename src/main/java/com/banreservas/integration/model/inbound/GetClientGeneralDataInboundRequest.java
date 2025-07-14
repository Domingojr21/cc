package com.banreservas.integration.model.inbound;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request inbound para el servicio ConsultarDatosGeneralesCliente MICM.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 08/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record GetClientGeneralDataInboundRequest(
        @JsonProperty("identificacion") String indentificationNumber,
        @JsonProperty("tipoIdentificacion") String identificationType,
        @JsonProperty("forzarActualizar") String forceUpdate,
        @JsonProperty("incluirFotoBinaria") String includeBinaryPhoto) implements Serializable {
}