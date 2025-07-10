package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response header with status information.
 */
@RegisterForReflection
public record HeaderResponseDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {
}