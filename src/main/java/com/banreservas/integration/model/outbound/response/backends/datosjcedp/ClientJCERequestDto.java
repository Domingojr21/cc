package com.banreservas.integration.model.outbound.response.backends.datosjcedp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.List;

/**
 * Client request data for JCE service.
 */
@RegisterForReflection
public record ClientJCERequestDto(
        @JsonProperty("identifications") List<IdentificationJCERequestDto> identifications
) implements Serializable {
}
