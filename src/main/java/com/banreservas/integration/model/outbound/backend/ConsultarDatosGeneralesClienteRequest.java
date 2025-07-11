package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import java.util.List;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ConsultarDatosGeneralesCliente.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteRequest(
        List<Client> clients,
        Boolean includeBinaryPhoto) implements Serializable {

    @RegisterForReflection
    public record Client(
            List<Identification> identifications) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            String number,
            String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request con una sola identificación.
     * 
     * @param identificationNumber número de identificación
     * @param identificationType tipo de identificación
     * @param includeBinaryPhoto incluir foto binaria
     * @return nueva instancia del request
     */
    public static ConsultarDatosGeneralesClienteRequest fromSingleIdentification(
            String identificationNumber, 
            String identificationType, 
            Boolean includeBinaryPhoto) {
        
        Identification identification = new Identification(identificationNumber, identificationType);
        Client client = new Client(List.of(identification));
        return new ConsultarDatosGeneralesClienteRequest(List.of(client), includeBinaryPhoto);
    }
}