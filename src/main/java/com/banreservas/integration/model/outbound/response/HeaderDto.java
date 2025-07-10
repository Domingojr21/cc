package com.banreservas.integration.model.outbound.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response header with status code and message.
 */
@RegisterForReflection
public record HeaderDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {
}
