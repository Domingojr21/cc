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
    private static final String DEFAULT_DATE = "0001-01-01T00:00:00";

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Iniciando mapeo de respuesta backend a formato MICM");
        
        GetClientGeneralDataResponse response = determineAndMapResponse(exchange);
        
        exchange.getIn().setBody(response);
        
        log.info("Mapeo de respuesta completado exitosamente");
    }

    private GetClientGeneralDataResponse determineAndMapResponse(Exchange exchange) throws Exception {
        if (hasJceResponse(exchange)) {
            String jceResponse = exchange.getProperty("jceResponse", String.class);
            return mapJceResponse(jceResponse);
        }
        
        if (hasMasterDataResponse(exchange)) {
            String masterResponse = exchange.getProperty("masterDataResponse", String.class);
            return mapMasterDataResponse(masterResponse);
        }
        
        if (hasLegalClientResponse(exchange)) {
            String legalResponse = exchange.getProperty("legalClientResponse", String.class);
            return mapLegalClientResponse(legalResponse);
        }
        
        throw new IllegalStateException("No se encontró respuesta válida de ningún servicio backend");
    }

    private boolean hasJceResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callJceService")) && 
               exchange.getProperty("jceResponse") != null;
    }

    private boolean hasMasterDataResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callMasterDataService")) && 
               exchange.getProperty("masterDataResponse") != null &&
               !Boolean.TRUE.equals(exchange.getProperty("clientNotFoundInMaster"));
    }

    private boolean hasLegalClientResponse(Exchange exchange) {
        return Boolean.TRUE.equals(exchange.getProperty("callLegalClientService")) && 
               exchange.getProperty("legalClientResponse") != null;
    }

    private GetClientGeneralDataResponse mapLegalClientResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio de cliente jurídico");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("client");
        JsonNode identification = client.path("identification");

        Identification identificacion = new Identification(
            getTextValueSafe(identification, "number"),
            getTextValueSafe(identification, "type")
        );

        return new GetClientGeneralDataResponse(
            identificacion,
            getTextValueSafe(client, "businessName"),
            "", "", DEFAULT_DATE,
            "", "", "", "", "", "", "", "", DEFAULT_DATE, "", ""
        );
    }

    private GetClientGeneralDataResponse mapMasterDataResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio de datos maestros");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("clients").get(0);
        JsonNode identification = client.path("identifications").get(0);

        Identification identificacion = new Identification(
            getTextValueSafe(identification, "number"),
            getTextValueSafe(identification, "type")
        );

        String birthDate = getTextValueSafe(client, "dateOfBirth");
        String cancellationDate = getTextValueSafe(client, "cancellationDate");

        return new GetClientGeneralDataResponse(
            identificacion,
            getTextValueSafe(client, "names"),
            getTextValueSafe(client, "firstName"),
            getTextValueSafe(client, "middleLastName"),
            isValidDate(birthDate) ? birthDate : DEFAULT_DATE,
            getTextValueSafe(client, "placeOfBirth"),
            "",
            getTextValueSafe(client, "sex"),
            getTextValueSafe(client, "maritalStatus"),
            getTextValueSafe(client, "category"),
            getTextValueSafe(client, "cancellationCause"),
            getTextValueSafe(client, "cancellationCauseID"),
            getTextValueSafe(client, "stateID"),
            isValidDate(cancellationDate) ? cancellationDate : DEFAULT_DATE,
            "",
            getTextValueSafe(client, "photo")
        );
    }

    private GetClientGeneralDataResponse mapJceResponse(String jsonResponse) throws Exception {
        log.info("Mapeando respuesta de servicio JCE");
        
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        JsonNode client = body.get("clients").get(0);
        JsonNode identification = client.path("identifications").get(0);

        Identification identificacion = new Identification(
            getTextValueSafe(identification, "number"),
            getTextValueSafe(identification, "type")
        );

        String birthDate = getTextValueSafe(client, "birthDate");
        String cancelDate = getTextValueSafe(client, "cancelDate");

        return new GetClientGeneralDataResponse(
            identificacion,
            getTextValueSafe(client, "names"),
            getTextValueSafe(client, "firstSurname"),
            getTextValueSafe(client, "secondSurname"),
            isValidDate(birthDate) ? birthDate : DEFAULT_DATE,
            getTextValueSafe(client, "birthPlace"),
            "",
            getTextValueSafe(client, "gender"),
            getTextValueSafe(client, "maritalStatus"),
            getTextValueSafe(client, "category"),
            getTextValueSafe(client, "cancelReason"),
            getTextValueSafe(client, "cancelReasonId"),
            getTextValueSafe(client, "stateId"),
            isValidDate(cancelDate) ? cancelDate : DEFAULT_DATE,
            "",
            getTextValueSafe(client, "photoBinary")
        );
    }

    private String getTextValueSafe(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return "";
        }
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return "";
        }
        String value = fieldNode.asText();
        return value != null ? value : "";
    }

    private boolean isValidDate(String dateString) {
        return dateString != null && !dateString.trim().isEmpty() && 
               !dateString.equals("0001-01-01") && !dateString.equals("0001-01-01T00:00:00");
    }
}