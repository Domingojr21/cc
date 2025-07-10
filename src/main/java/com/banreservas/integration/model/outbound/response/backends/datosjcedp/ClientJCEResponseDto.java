package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Client response data from JCE with detailed personal information.
 */
@RegisterForReflection
public record ClientJCEResponseDto(
        @JsonProperty("identifications") List<IdentificationJCEResponseDto> identifications,
        @JsonProperty("names") String names,
        @JsonProperty("firstSurname") String firstSurname,
        @JsonProperty("secondSurname") String secondSurname,
        @JsonProperty("birthDate") String birthDate,
        @JsonProperty("birthPlace") String birthPlace,
        @JsonProperty("gender") String gender,
        @JsonProperty("maritalStatus") String maritalStatus,
        @JsonProperty("categoryId") String categoryId,
        @JsonProperty("category") String category,
        @JsonProperty("cancelReasonId") String cancelReasonId,
        @JsonProperty("cancelReason") String cancelReason,
        @JsonProperty("stateId") String stateId,
        @JsonProperty("state") String state,
        @JsonProperty("cancelDate") String cancelDate,
        @JsonProperty("nationCode") String nationCode,
        @JsonProperty("nationality") String nationality,
        @JsonProperty("expirationDate") String expirationDate
) implements Serializable {
}

