package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Client response data with detailed personal information.
 */
@RegisterForReflection
public record ClientMaestroResponseDto(
        @JsonProperty("identifications") List<IdentificationMaestroResponseDto> identifications,
        @JsonProperty("names") String names,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("middleName") String middleName,
        @JsonProperty("middleLastName") String middleLastName,
        @JsonProperty("middleSecondLastName") String middleSecondLastName,
        @JsonProperty("lastNames") String lastNames,
        @JsonProperty("dateOfBirth") String dateOfBirth,
        @JsonProperty("placeOfBirth") String placeOfBirth,
        @JsonProperty("sex") String sex,
        @JsonProperty("maritalStatus") String maritalStatus,
        @JsonProperty("categoryId") int categoryId,
        @JsonProperty("category") String category,
        @JsonProperty("cancellationCauseID") String cancellationCauseID,
        @JsonProperty("cancellationCause") String cancellationCause,
        @JsonProperty("stateID") String stateID,
        @JsonProperty("cancellationDate") String cancellationDate,
        @JsonProperty("idMunicipality") int idMunicipality,
        @JsonProperty("nationalities") List<NationalityDto> nationalities,
        @JsonProperty("lastUpdateDate") String lastUpdateDate,
        @JsonProperty("photo") String photo
) implements Serializable {
}
