package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Clients wrapper for JCE service request.
 */
@RegisterForReflection
public record ClientsJCERequestDto(
        @JsonProperty("client") List<ClientJCERequestDto> client
) implements Serializable {
}
