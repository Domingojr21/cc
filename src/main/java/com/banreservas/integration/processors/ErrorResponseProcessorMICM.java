package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.GetClientGeneralDataResponse;
import com.banreservas.integration.model.inbound.GetClientGeneralDataResponse.Identification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Procesador para generar respuestas de error en formato JSON según el protocolo MICM.
 * Maneja la construcción de respuestas de error estandarizadas cuando ocurren excepciones.
 *
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ErrorResponseProcessorMICM implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ErrorResponseProcessorMICM.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Genera una respuesta JSON de error basada en la excepción ocurrida.
     * 
     * @param exchange el intercambio de Camel que contiene la información del error
     * @throws Exception si ocurre un error durante el procesamiento
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        ErrorInfo errorInfo = extractErrorInfo(exchange);
        
        ObjectNode errorResponse = buildErrorResponse(errorInfo);
        
        setResponseInExchange(exchange, errorResponse, errorInfo.httpCode());
        setHttpHeaders(exchange, errorInfo.httpCode());
        preserveSessionId(exchange);

        log.info("Respuesta de error JSON MICM generada - Código: {}, Mensaje: {}", 
                errorInfo.httpCode(), errorInfo.message());
    }

    /**
     * Extrae la información de error del exchange.
     */
    private ErrorInfo extractErrorInfo(Exchange exchange) {
        Integer httpCode = determineHttpCode(exchange);
        String message = determineErrorMessage(exchange);
        
        return new ErrorInfo(httpCode, message);
    }

    /**
     * Determina el código HTTP del error.
     */
    private Integer determineHttpCode(Exchange exchange) {
        // Intentar obtener del property backendErrorCode
        String backendErrorCodeStr = getPropertySafely(exchange, "backendErrorCode");
        if (backendErrorCodeStr != null) {
            try {
                return Integer.parseInt(backendErrorCodeStr);
            } catch (NumberFormatException e) {
                log.warn("Error parseando backendErrorCode: {}", backendErrorCodeStr);
            }
        }

        // Intentar obtener del header HTTP
        Integer headerCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
        if (headerCode != null) {
            return headerCode;
        }

        return 500; // Código por defecto
    }

    /**
     * Determina el mensaje de error.
     */
    private String determineErrorMessage(Exchange exchange) {
        String message = getPropertySafely(exchange, "backendErrorMessage");
        if (isValidMessage(message)) {
            return message;
        }

        message = getPropertySafely(exchange, "Mensaje");
        if (isValidMessage(message)) {
            return message;
        }

        return "Error interno del servidor";
    }

    /**
     * Verifica si un mensaje es válido (no nulo y no vacío).
     */
    private boolean isValidMessage(String message) {
        return message != null && !message.trim().isEmpty();
    }

    /**
     * Construye la respuesta de error en formato ObjectNode.
     */
    private ObjectNode buildErrorResponse(ErrorInfo errorInfo) {
        Identification emptyIdentification = new Identification("", "");

        GetClientGeneralDataResponse baseResponse = new GetClientGeneralDataResponse(
                emptyIdentification,
                "", "", "", "0001-01-01", "", "", "", "",
                "", "", "", "", "0001-01-01", "", "");

        ObjectNode errorNode = mapper.valueToTree(baseResponse);
        
        // Agregar campos específicos de error
        errorNode.put("mensaje", errorInfo.message());
        errorNode.put("codigo", errorInfo.httpCode());

        return errorNode;
    }

    /**
     * Establece la respuesta en el exchange.
     */
    private void setResponseInExchange(Exchange exchange, ObjectNode errorResponse, Integer httpCode) {
        exchange.getIn().setBody(errorResponse);
        exchange.getMessage().setBody(errorResponse);
    }

    /**
     * Configura los headers HTTP de respuesta.
     */
    private void setHttpHeaders(Exchange exchange, Integer httpCode) {
        String statusText = getHttpStatusText(httpCode);
        
        // Headers principales de Camel
        exchange.getIn().setHeader("CamelHttpResponseCode", httpCode);
        exchange.getIn().setHeader("CamelHttpResponseText", statusText);
        exchange.getIn().setHeader("Content-Type", "application/json");

        // Headers para el mensaje de salida
        exchange.getMessage().setHeader("CamelHttpResponseCode", httpCode);
        exchange.getMessage().setHeader("CamelHttpResponseText", statusText);
        exchange.getMessage().setHeader("Content-Type", "application/json");

    }

    /**
     * Preserva el sessionId original en la respuesta.
     */
    private void preserveSessionId(Exchange exchange) {
        String sessionId = getHeaderSafely(exchange, "sessionId");
        if (sessionId != null) {
            exchange.getIn().setHeader("sessionId", sessionId);
        }
    }

    /**
     * Obtiene el texto del status HTTP para el código dado.
     */
    private String getHttpStatusText(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "Unknown";
        };
    }

    /**
     * Obtiene una propiedad del exchange de forma segura.
     */
    private String getPropertySafely(Exchange exchange, String propertyName) {
        try {
            Object value = exchange.getProperty(propertyName);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            log.warn("Error obteniendo propiedad {}: {}", propertyName, e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene un header del exchange de forma segura.
     */
    private String getHeaderSafely(Exchange exchange, String headerName) {
        try {
            Object value = exchange.getIn().getHeader(headerName);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            log.warn("Error obteniendo header {}: {}", headerName, e.getMessage());
            return null;
        }
    }

    /**
     * Record que encapsula la información de error.
     */
    private record ErrorInfo(Integer httpCode, String message) {}
}