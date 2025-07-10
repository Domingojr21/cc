package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Response header for JCE service.
 */
@RegisterForReflection
public record HeaderJCEResponseDto(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("responseMessage") String responseMessage
) implements Serializable {
}
