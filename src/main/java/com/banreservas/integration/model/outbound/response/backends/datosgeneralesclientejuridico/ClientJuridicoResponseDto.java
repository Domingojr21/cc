package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Juridical client response data with business information.
 */
@RegisterForReflection
public record ClientJuridicoResponseDto(
        @JsonProperty("identification") IdentificationResponseDto identification,
        @JsonProperty("businessName") String businessName,
        @JsonProperty("tradeName") String tradeName
) implements Serializable {
}