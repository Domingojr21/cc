package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ConsultarDatosGeneralesClienteJuridico.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 09/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record GetLegalClientGeneralDataRequest(
        @JsonProperty("client") Client client) implements Serializable {

    @RegisterForReflection
    public record Client(
            @JsonProperty("identification") Identification identification) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            @JsonProperty("number") String number,
            @JsonProperty("type") String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request con identificación.
     * 
     * @param identificationNumber número de identificación
     * @param identificationType tipo de identificación
     * @return nueva instancia del request
     */
    public static GetLegalClientGeneralDataRequest fromIdentification(
            @JsonProperty("identificationNumber") String identificationNumber, 
            @JsonProperty("identificationType") String identificationType) {
        
        Identification identification = new Identification(identificationNumber, identificationType);
        Client client = new Client(identification);
        return new GetLegalClientGeneralDataRequest(client);
    }
}