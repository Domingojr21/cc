package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.GetClientGeneralDataInboundResponse;
import com.banreservas.integration.model.inbound.GetClientGeneralDataInboundResponse.Identificacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para generar respuestas de error en formato JSON según el protocolo
 * MICM.
 * Se utiliza cuando ocurren excepciones durante el procesamiento.
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
     * Genera una respuesta JSON de error basada en la excepción ocurrida
     * 
     * @param exchange el intercambio de Camel que contiene la información del error
     * @throws Exception si ocurre un error durante el procesamiento
     */
    @Override
    public void process(Exchange exchange) throws Exception {

        Integer backendHttpCode = null;
        String backendErrorCodeStr = getPropertySafely(exchange, "backendErrorCode");
        if (backendErrorCodeStr != null) {
            try {
                backendHttpCode = Integer.parseInt(backendErrorCodeStr);
            } catch (NumberFormatException e) {
                log.warn("Error parseando backendErrorCode: {}", backendErrorCodeStr);
            }
        }

        if (backendHttpCode == null) {
            // Intentar obtener del header original
            backendHttpCode = exchange.getIn().getHeader("CamelHttpResponseCode", Integer.class);
        }

        if (backendHttpCode == null) {
            backendHttpCode = 500; // Código por defecto
        }

        // Obtener mensaje de error del backend
        String mensaje = getPropertySafely(exchange, "backendErrorMessage");
        if (mensaje == null || mensaje.trim().isEmpty()) {
            mensaje = getPropertySafely(exchange, "Mensaje");
            if (mensaje == null || mensaje.trim().isEmpty()) {
                mensaje = "Error interno del servidor";
            }
        }

        Identificacion identificacion = new Identificacion("", "");

        GetClientGeneralDataInboundResponse errorResponse = new GetClientGeneralDataInboundResponse(
                identificacion,
                "", "", "", "0001-01-01", "", "", "", "",
                "", "", "", "", "0001-01-01", "", "");

        // Convertir a ObjectNode para agregar campos de error adicionales
        ObjectNode errorNode = mapper.valueToTree(errorResponse);

        // Agregar campos de error específicos
        errorNode.put("mensaje", mensaje);
        errorNode.put("codigo", backendHttpCode);

        exchange.getIn().setBody(errorNode);

        // Headers principales de Camel
        exchange.getIn().setHeader("CamelHttpResponseCode", backendHttpCode);
        exchange.getIn().setHeader("CamelHttpResponseText", getHttpStatusText(backendHttpCode));
        exchange.getIn().setHeader("Content-Type", "application/json");

        // Headers específicos para Servlet
        exchange.getIn().setHeader("HTTP_RESPONSE_CODE", backendHttpCode);

        // Configurar en el message de salida también
        exchange.getMessage().setHeader("CamelHttpResponseCode", backendHttpCode);
        exchange.getMessage().setHeader("CamelHttpResponseText", getHttpStatusText(backendHttpCode));
        exchange.getMessage().setHeader("Content-Type", "application/json");
        exchange.getMessage().setBody(exchange.getIn().getBody());

        HttpServletResponse httpResponse = exchange.getIn().getHeader("CamelHttpServletResponse",
                HttpServletResponse.class);
        if (httpResponse != null) {
            httpResponse.setStatus(backendHttpCode);
            httpResponse.setContentType("application/json");
            log.info("Código HTTP configurado directamente en HttpServletResponse: {}", backendHttpCode);
        }

        String sessionId = getHeaderSafely(exchange, "sessionId");
        if (sessionId != null) {
            exchange.getIn().setHeader("sessionId", sessionId);
        }

        log.info("Respuesta de error JSON MICM generada con - Código: {}, Mensaje: {}", backendHttpCode, mensaje);
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
     * Obtiene una propiedad del exchange de forma segura (sin lanzar excepción).
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
     * Obtiene un header del exchange de forma segura (sin lanzar excepción).
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
}