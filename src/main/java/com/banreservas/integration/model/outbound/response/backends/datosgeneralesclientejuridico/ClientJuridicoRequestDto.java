package com.banreservas.integration.model.outbound.response.backends.datosgeneralesclientejuridico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Client request data for juridical client queries.
 */
@RegisterForReflection
public record ClientJuridicoRequestDto(
        @JsonProperty("identification") IdentificationRequestDto identification
) implements Serializable {
}