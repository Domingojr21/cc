package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.GetClientGeneralDataResponse;
import com.banreservas.integration.model.inbound.GetClientGeneralDataResponse.Identification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para mapear las respuestas de los servicios backend al formato de respuesta MICM.
 * Convierte las respuestas JSON de diferentes servicios backend al objeto de respuesta unificado.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class BackendResponseMappingProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(BackendResponseMappingProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Mapea la respuesta del servicio backend ejecutado al formato de respuesta MICM.
     * 
     * @param exchange el intercambio de Camel que contiene las respuestas de los servicios
     * @throws Exception si ocurre un error durante el mapeo
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Iniciando mapeo de respuesta backend a formato MICM");
        
        GetClientGeneralDataResponse response = determineAndMapResponse(exchange);
        
        exchange.getIn().setBody(response);
        
        log.info("Mapeo de respuesta completado exitosamente");
    }

    /**
     * Determina cuál respuesta mapear según la prioridad de servicios ejecutados.
     */
    private GetClientGeneralDataResponse determineAndMapResponse(Exchange exchange) throws Exception {
        // Prioridad 1: Respuesta JCE (más actualizada)
        if (hasJceResponse(exchange)) {
            String jceResponse = exchange.getProperty("jceResponse", String.class);
            return mapJceResponse(jceResponse);
        }
        
        // Prioridad 2: Respuesta Maestro (si no hubo error 904)
        if (hasMasterDataResponse(exchange)) {
            String masterResponse = exchange.getProperty("masterDataResponse", String.class);
            return mapMasterDataResponse(masterResponse);
        }
        
        // Prioridad 3: Respuesta Cliente Jurídico
        if (hasLegalClientResponse(exchange)) {
            String legalResponse = exchange.getProperty("legalClientResponse", String.class);
            return mapLegalClientResponse(legalResponse);
        }
        
        throw new IllegalStateException("No se encontró respuesta válida de ningún servicio backend");
    }

    /**
     * Verifica si existe respuesta JCE válida.
     */
    private boolean hasJceResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callJceService")) && 
               exchange.getProperty("jceResponse") != null;
    }

    /**
     * Verifica si existe respuesta de datos maestros válida.
     */
    private boolean hasMasterDataResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callMasterDataService")) && 
               exchange.getProperty("masterDataResponse") != null &&
               !Boolean.TRUE.equals(exchange.getProperty("clientNotFoundInMaster"));
    }

    /**
     * Verifica si existe respuesta de cliente jurídico válida.
     */
    private boolean hasLegalClientResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callLegalClientService")) && 
               exchange.getProperty("legalClientResponse") != null;
    }

    /**
     * Mapea la respuesta del servicio de cliente jurídico.
     */
    private GetClientGeneralDataResponse mapLegalClientResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio de cliente jurídico");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("client");
        JsonNode identification = client.path("identification");

        Identification identificacion = new Identification(
            identification.path("number").textValue(),
            identification.path("type").textValue()
        );

        return new GetClientGeneralDataResponse(
            identificacion,
            client.path("businessName").textValue(),
            "", "", "0001-01-01T00:00:00",
            "", "", "", "", "", "", "", "", "0001-01-01T00:00:00", "", ""
        );
    }

    /**
     * Mapea la respuesta del servicio de datos maestros.
     */
    private GetClientGeneralDataResponse mapMasterDataResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio de datos maestros");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("clients").get(0);
        JsonNode identification = client.path("identifications").get(0);

        Identification identificacion = new Identification(
            identification.path("number").textValue(),
            identification.path("type").textValue()
        );

        String birthDate = client.path("dateOfBirth").textValue();
        String cancellationDate = client.path("cancellationDate").textValue();

        return new GetClientGeneralDataResponse(
            identificacion,
            client.path("names").textValue(),
            client.path("firstName").textValue(),
            client.path("middleLastName").textValue(),
            birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
            client.path("placeOfBirth").textValue(),
            "",
            client.path("sex").textValue(),
            client.path("maritalStatus").textValue(),
            client.path("category").textValue(),
            client.path("cancellationCause").textValue(),
            client.path("cancellationCauseID").textValue(),
            client.path("stateID").textValue(),
            cancellationDate.isEmpty() ? "0001-01-01T00:00:00" : cancellationDate,
            "",
            client.path("photo").textValue()
        );
    }

    /**
     * Mapea la respuesta del servicio JCE.
     */
    private GetClientGeneralDataResponse mapJceResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio JCE");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("clients").get(0);
        JsonNode identification = client.path("identifications").get(0);

        Identification identificacion = new Identification(
            identification.path("number").textValue(),
            identification.path("type").textValue()
        );

        String birthDate = client.path("birthDate").textValue();
        String cancelDate = client.path("cancelDate").textValue();

        return new GetClientGeneralDataResponse(
            identificacion,
            client.path("names").textValue(),
            client.path("firstSurname").textValue(),
            client.path("secondSurname").textValue(),
            birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
            client.path("birthPlace").textValue(),
            "",
            client.path("gender").textValue(),
            client.path("maritalStatus").textValue(),
            client.path("category").textValue(),
            client.path("cancelReason").textValue(),
            client.path("cancelReasonId").textValue(),
            client.path("stateId").textValue(),
            cancelDate.isEmpty() ? "0001-01-01T00:00:00" : cancelDate,
            "",
            client.path("photoBinary").textValue()
        );
    }
}