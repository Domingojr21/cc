package com.banreservas.integration.model.outbound.backend;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Request DTO para el servicio ActualizarDatosMaestroCedulados.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 09/07/2025
 * @version 1.0.0
 */
@RegisterForReflection
public record UpdateMasterCedulatedDataRequest(
        List<Client> clients) implements Serializable {

    @RegisterForReflection
    public record Client(
            @JsonProperty("identifications") List<Identification> identifications,
            @JsonProperty("name") String name,
            @JsonProperty("firstLastName") String firstLastName,
            @JsonProperty("secondLastName") String secondLastName,
            @JsonProperty("birthDate") String birthDate,
            @JsonProperty("birthPlace") String birthPlace,
            @JsonProperty("gender") String gender,
            @JsonProperty("civilStatus") String civilStatus,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("category") String category,
            @JsonProperty("cancellationnCauseId") String cancellationnCauseId,
            @JsonProperty("cancellationCause") String cancellationCause,
            @JsonProperty("statusId") String statusId,
            @JsonProperty("status") String status,
            @JsonProperty("cancellationDate") String cancellationDate,
            @JsonProperty("nationCode") String nationCode,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("binaryPhoto") String binaryPhoto,
            @JsonProperty("expirationDate") String expirationDate) implements Serializable {
    }

    @RegisterForReflection
    public record Identification(
            @JsonProperty("number") String number,
            @JsonProperty("type") String type) implements Serializable {
    }

    /**
     * Constructor conveniente para crear request desde los datos del cliente JCE.
     * 
     * @param jceClientData datos del cliente desde JCE
     * @return nueva instancia del request
     */
    public static UpdateMasterCedulatedDataRequest fromJCEClientData(
            @JsonProperty("identificationNumber") String identificationNumber,
            @JsonProperty("identificationType") String identificationType,
            @JsonProperty("names") String names,
            @JsonProperty("firstSurname") String firstSurname,
            @JsonProperty("secondSurname") String secondSurname,
            @JsonProperty("birthDate") String birthDate,
            @JsonProperty("birthPlace") String birthPlace,
            @JsonProperty("gender") String gender,
            @JsonProperty("maritalStatus") String maritalStatus,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("category") String category,
            @JsonProperty("cancelReasonId") String cancelReasonId,
            @JsonProperty("cancelReason") String cancelReason,
            @JsonProperty("stateId") String stateId,
            @JsonProperty("state") String state,
            @JsonProperty("cancelDate") String cancelDate,
            @JsonProperty("nationCode") String nationCode,
            @JsonProperty("nationality") String nationality,
            @JsonProperty("binaryPhoto") String binaryPhoto,  
            @JsonProperty("expirationDate") String expirationDate) {
        
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
            binaryPhoto, 
            expirationDate
        );
        return new UpdateMasterCedulatedDataRequest(List.of(client));
    }
}