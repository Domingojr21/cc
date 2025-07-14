package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.inbound.GetClientGeneralDataRequest;
import com.banreservas.integration.util.BooleanValueUtil;
import com.banreservas.integration.util.Constants;
import com.banreservas.integration.util.IdentificationTypeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para validar requests de consulta de datos generales de cliente MICM.
 * Implementa validaciones de negocio y normalización de datos de entrada.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class RequestValidationProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(RequestValidationProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Valida y normaliza el request de entrada según las reglas de negocio.
     * 
     * @param exchange el intercambio de Camel que contiene el request
     * @throws ValidationException si la validación falla
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        
        String requestBody = exchange.getIn().getBody(String.class);
        
        if (requestBody == null || requestBody.trim().isEmpty()) {
            throw new ValidationException("Request body no puede ser nulo o vacío");
        }

        GetClientGeneralDataRequest request = deserializeRequest(requestBody);
        
        String identificationNumber = validateAndNormalizeIdentificationNumber(request.indentificationNumber());
        String identificationType = validateAndNormalizeIdentificationType(request.identificationType());
        
        validateIdentificationFormat(identificationNumber, identificationType);

        String forceUpdate = normalizeForceUpdate(request.forceUpdate());
        String includeBinaryPhoto = normalizeIncludeBinaryPhoto(request.includeBinaryPhoto());

        setValidatedPropertiesInExchange(exchange, request, identificationNumber, identificationType, forceUpdate, includeBinaryPhoto);
        setOriginalHeadersInExchange(exchange);

        log.info("Validación exitosa para identificación: {} de tipo: {}", identificationNumber, identificationType);
    }

    /**
     * Deserializa el JSON request al objeto correspondiente.
     */
    private GetClientGeneralDataRequest deserializeRequest(String requestBody) throws ValidationException {
        try {
            return mapper.readValue(requestBody, GetClientGeneralDataRequest.class);
        } catch (Exception e) {
            throw new ValidationException("Formato JSON inválido: " + e.getMessage());
        }
    }

    /**
     * Valida y normaliza el número de identificación.
     */
    private String validateAndNormalizeIdentificationNumber(String identificationNumber) throws ValidationException {
        if (identificationNumber == null || identificationNumber.trim().isEmpty()) {
            throw new ValidationException("Identificación es requerida");
        }
        return identificationNumber.trim();
    }

    /**
     * Valida y normaliza el tipo de identificación.
     */
    private String validateAndNormalizeIdentificationType(String identificationType) throws ValidationException {
        if (identificationType == null || identificationType.trim().isEmpty()) {
            throw new ValidationException("Tipo de identificación es requerido");
        }

        String normalizedType = IdentificationTypeUtil.normalize(identificationType);
        if (normalizedType == null) {
            throw new ValidationException("Tipo de identificación debe ser: Cedula o RNC");
        }
        return normalizedType;
    }

    /**
     * Valida el formato del número de identificación según su tipo.
     */
    private void validateIdentificationFormat(String identificationNumber, String identificationType) throws ValidationException {
        if (Constants.IDENTIFICATION_TYPE_CEDULA.equals(identificationType)) {
            validateCedulaFormat(identificationNumber);
        } else if (Constants.IDENTIFICATION_TYPE_RNC.equals(identificationType)) {
            validateRncFormat(identificationNumber);
        }
    }

    /**
     * Valida el formato de la cédula.
     */
    private void validateCedulaFormat(String cedula) throws ValidationException {
        if (!cedula.matches("^[0-9]+$")) {
            throw new ValidationException("Número de cédula debe contener solo dígitos numéricos");
        }
        
        if (cedula.length() != 11) {
            throw new ValidationException("Número de cédula debe tener exactamente 11 dígitos");
        }
    }

    /**
     * Valida el formato del RNC.
     */
    private void validateRncFormat(String rnc) throws ValidationException {
        if (!rnc.matches("^[0-9]+$")) {
            throw new ValidationException("Número de RNC debe contener solo dígitos numéricos");
        }
        
        if (rnc.length() < 9 || rnc.length() > 11) {
            throw new ValidationException("Número de RNC debe tener entre 9 y 11 dígitos");
        }
    }

    /**
     * Normaliza el valor de forzar actualización.
     */
    private String normalizeForceUpdate(String forceUpdate) throws ValidationException {
        String originalValue = forceUpdate != null ? forceUpdate : Constants.BOOLEAN_FALSE;
        String normalizedValue = BooleanValueUtil.normalize(originalValue);
        
        if (normalizedValue == null) {
            throw new ValidationException("forzarActualizar debe ser TRUE o FALSE");
        }
        return normalizedValue;
    }

    /**
     * Normaliza el valor de incluir foto binaria.
     */
    private String normalizeIncludeBinaryPhoto(String includeBinaryPhoto) throws ValidationException {
        String originalValue = includeBinaryPhoto != null ? includeBinaryPhoto : Constants.BOOLEAN_FALSE;
        String normalizedValue = BooleanValueUtil.normalize(originalValue);
        
        if (normalizedValue == null) {
            throw new ValidationException("incluirFotoBinaria debe ser TRUE o FALSE");
        }
        return normalizedValue;
    }

    /**
     * Establece las propiedades validadas en el exchange.
     */
    private void setValidatedPropertiesInExchange(Exchange exchange, GetClientGeneralDataRequest request,
            String identificationNumber, String identificationType, String forceUpdate, String includeBinaryPhoto) {
        
        exchange.setProperty("identificationNumberRq", identificationNumber);
        exchange.setProperty("identificationTypeRq", identificationType);
        exchange.setProperty("forceUpdateRq", forceUpdate);
        exchange.setProperty("includeBinaryPhotoRq", includeBinaryPhoto);
        exchange.setProperty("inboundRequest", request);
    }

    /**
     * Guarda los headers originales del request en el exchange.
     */
    private void setOriginalHeadersInExchange(Exchange exchange) {
        exchange.setProperty("originalSessionId", exchange.getIn().getHeader("sessionId"));
        exchange.setProperty("canalRq", exchange.getIn().getHeader("Canal"));
        exchange.setProperty("usuarioRq", exchange.getIn().getHeader("Usuario"));
        exchange.setProperty("terminalRq", exchange.getIn().getHeader("Terminal"));
        exchange.setProperty("fechaHoraRq", exchange.getIn().getHeader("FechaHora"));
        exchange.setProperty("versionRq", exchange.getIn().getHeader("Version"));
        exchange.setProperty("servicioRq", exchange.getIn().getHeader("Servicio"));
    }
}