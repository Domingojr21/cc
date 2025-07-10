package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Response body containing client data from JCE service.
 */
@RegisterForReflection
public record BodyJCEResponseDto(
        @JsonProperty("clients") List<ClientJCEResponseDto> clients
) implements Serializable {
}
