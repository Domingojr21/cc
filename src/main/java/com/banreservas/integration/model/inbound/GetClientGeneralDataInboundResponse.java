package com.banreservas.integration.model.inbound;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Response inbound para el servicio ConsultarDatosGeneralesCliente MICM.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 08/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record GetClientGeneralDataInboundResponse(
        @JsonProperty("identificacion") Identificacion identificacion,
        @JsonProperty("nombres") String names,
        @JsonProperty("primerApellido") String firstLastName,
        @JsonProperty("segundoApellido") String secondLastName,
        @JsonProperty("fechaNacimiento") String birthDate,
        @JsonProperty("lugarNacimiento") String birthPlace,
        @JsonProperty("cedulaVieja") String oldIdCard,
        @JsonProperty("sexo") String gender,
        @JsonProperty("estadoCivil") String maritalStatus,
        @JsonProperty("categoria") String category,
        @JsonProperty("causaInhabilidad") String disqualificationReason,
        @JsonProperty("codigoCausaCancelacion") String cancellationReasonCode,
        @JsonProperty("estatus") String status,
        @JsonProperty("fechaCancelacion") String cancellationDate,
        @JsonProperty("fotoUrl") String photoUrl,
        @JsonProperty("fotoBinario") String photoBinary) implements Serializable {

    @RegisterForReflection
    public record Identificacion(
            @JsonProperty("numeroIdentificacion") String identificationNumber,
            @JsonProperty("tipoIdentificacion") String identificationType) implements Serializable {
    }
}