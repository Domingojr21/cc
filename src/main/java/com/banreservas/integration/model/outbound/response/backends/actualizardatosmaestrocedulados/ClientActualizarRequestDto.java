package com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Client request data for updating master cedula information.
 */
@RegisterForReflection
public record ClientActualizarRequestDto(
        @JsonProperty("identifications") List<IdentificationActualizarRequestDto> identifications,
        @JsonProperty("names") String names,
        @JsonProperty("firstLastName") String firstLastName,
        @JsonProperty("secondLastName") String secondLastName,
        @JsonProperty("birthDate") String birthDate,
        @JsonProperty("birthPlace") String birthPlace,
        @JsonProperty("gender") String gender,
        @JsonProperty("civilStatus") String civilStatus,
        @JsonProperty("categoryId") String categoryId,
        @JsonProperty("category") String category,
        @JsonProperty("cancellationnCauseId") String cancellationnCauseId,
        @JsonProperty("cancellationCause") String cancellationCause,
        @JsonProperty("statusId") String statusId,
        @JsonProperty("status") String status,
        @JsonProperty("cancellationDate") String cancellationDate,
        @JsonProperty("nationCode") String nationCode,
        @JsonProperty("nationality") String nationality,
        @JsonProperty("binaryPhoto") String binaryPhoto,
        @JsonProperty("expirationDate") String expirationDate
) implements Serializable {
}

