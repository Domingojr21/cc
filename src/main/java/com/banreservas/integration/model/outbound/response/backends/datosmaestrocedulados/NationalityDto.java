package com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

/**
 * Nationality information.
 */
@RegisterForReflection
public record NationalityDto(
        @JsonProperty("code") String code,
        @JsonProperty("nationality") String nationality
) implements Serializable {
}
