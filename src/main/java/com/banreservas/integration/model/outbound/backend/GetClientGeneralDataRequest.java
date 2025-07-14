package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ConsultarDatosGeneralesCliente.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 09/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record GetClientGeneralDataRequest(
        @JsonProperty("clients") List<Client> clients,
        @JsonProperty("includeBinaryPhoto") Boolean includeBinaryPhoto) implements Serializable {

    @RegisterForReflection
    public record Client(
            @JsonProperty("identifications") List<Identification> identifications) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            @JsonProperty("number") String number,
            @JsonProperty("type") String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request con una sola identificación.
     * 
     * @param identificationNumber número de identificación
     * @param identificationType tipo de identificación
     * @param includeBinaryPhoto incluir foto binaria
     * @return nueva instancia del request
     */
    public static GetClientGeneralDataRequest fromSingleIdentification(
            @JsonProperty("identificationNumber")String identificationNumber, 
            @JsonProperty("identificationType") String identificationType, 
            @JsonProperty("includeBinaryPhoto") Boolean includeBinaryPhoto) {
        
        Identification identification = new Identification(identificationNumber, identificationType);
        Client client = new Client(List.of(identification));
        return new GetClientGeneralDataRequest(List.of(client), includeBinaryPhoto);
    }
}