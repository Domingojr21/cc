package com.banreservas.integration.model.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Client data with identification and business information.
 */
@RegisterForReflection
public record ClientDto(
        @JsonProperty("identification") IdentificationDto identification,
        @JsonProperty("businessName") String businessName,
        @JsonProperty("tradeName") String tradeName
) implements Serializable {
}
