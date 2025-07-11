package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ConsultarDatosGeneralesClienteJuridico.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record ConsultarDatosGeneralesClienteJuridicoRequest(
        Client client) implements Serializable {

    @RegisterForReflection
    public record Client(
            Identification identification) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            String number,
            String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request con identificación.
     * 
     * @param identificationNumber número de identificación
     * @param identificationType tipo de identificación
     * @return nueva instancia del request
     */
    public static ConsultarDatosGeneralesClienteJuridicoRequest fromIdentification(
            String identificationNumber, 
            String identificationType) {
        
        Identification identification = new Identification(identificationNumber, identificationType);
        Client client = new Client(identification);
        return new ConsultarDatosGeneralesClienteJuridicoRequest(client);
    }
}