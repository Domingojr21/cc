package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import java.util.List;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ActualizarDatosMaestroCedulados.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record ActualizarDatosMaestroCeduladosRequest(
        List<Client> clients) implements Serializable {

    @RegisterForReflection
    public record Client(
            List<Identification> identifications,
            String name,
            String firstLastName,
            String secondLastName,
            String birthDate,
            String birthPlace,
            String gender,
            String civilStatus,
            String categoryId,
            String category,
            String cancellationnCauseId,
            String cancellationCause,
            String statusId,
            String status,
            String cancellationDate,
            String nationCode,
            String nationality,
            String binaryPhoto,
            String expirationDate) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            String number,
            String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request desde los datos del cliente JCE.
     * 
     * @param jceClientData datos del cliente desde JCE
     * @return nueva instancia del request
     */
    public static ActualizarDatosMaestroCeduladosRequest fromJCEClientData(
            String identificationNumber,
            String identificationType,
            String names,
            String firstSurname,
            String secondSurname,
            String birthDate,
            String birthPlace,
            String gender,
            String maritalStatus,
            String categoryId,
            String category,
            String cancelReasonId,
            String cancelReason,
            String stateId,
            String state,
            String cancelDate,
            String nationCode,
            String nationality,
            String binaryPhoto,  
            String expirationDate) {
        
        Identification identification = new Identification(identificationNumber, identificationType);
        Client client = new Client(
            List.of(identification),
            names,
            firstSurname,
            secondSurname,
            birthDate,
            birthPlace,
            gender,
            maritalStatus,
            categoryId,
            category,
            cancelReasonId,
            cancelReason,
            stateId,
            state,
            cancelDate,
            nationCode,
            nationality,
            binaryPhoto, // binaryPhoto
            expirationDate
        );
        return new ActualizarDatosMaestroCeduladosRequest(List.of(client));
    }
}