package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Response body for juridical client data.
 */
@RegisterForReflection
public record BodyJuridicoResponseDto(
        @JsonProperty("client") ClientJuridicoResponseDto client
) implements Serializable {
}