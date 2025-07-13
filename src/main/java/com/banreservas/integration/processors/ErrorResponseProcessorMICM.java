package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.ConsultarDatosGeneralesClienteInboundResponse;
import com.banreservas.integration.model.inbound.ConsultarDatosGeneralesClienteInboundResponse.Identificacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletResponse;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para generar respuestas de error en formato JSON según el protocolo MICM.
 * Se utiliza cuando ocurren excepciones durante el procesamiento.
 * Ahora con type safety y estructura anidada.
 *
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ErrorResponseProcessorMICM implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ErrorResponseProcessorMICM.class);
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Genera una respuesta JSON de error basada en la excepción ocurrida,
     * siguiendo el formato MICM con type safety y estructura anidada.
     * 
     * @param exchange el intercambio de Camel que contiene la información del error
     * @throws Exception si ocurre un error durante el procesamiento
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Generando respuesta de error JSON MICM con type safety");

        // Obtener código HTTP del backend
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

        // *** USAR TYPE SAFETY PARA RESPUESTA DE ERROR ***
        try {
            // Crear respuesta de error usando la clase inbound + campos adicionales
            Identificacion identificacion = new Identificacion("", "");
            
            ConsultarDatosGeneralesClienteInboundResponse errorResponse = 
                new ConsultarDatosGeneralesClienteInboundResponse(
                    identificacion,
                    "", "", "", "0001-01-01T00:00:00", "", "", "", "", 
                    "", "", "", "", "0001-01-01T00:00:00", "", ""
                );
            
            // Convertir a ObjectNode para agregar campos de error adicionales
            ObjectNode errorNode = mapper.valueToTree(errorResponse);
            
            // Agregar campos de error específicos
            errorNode.put("error", mensaje);
            errorNode.put("errorCode", backendHttpCode);
            
            exchange.getIn().setBody(errorNode);
            
            log.info("Respuesta de error type-safe generada exitosamente");
            
        } catch (Exception e) {
            log.error("Error creando respuesta type-safe, usando fallback ObjectNode", e);
            
            // Fallback a ObjectNode si falla el type-safe
            ObjectNode errorResponse = mapper.createObjectNode();
            
            // Crear objeto identificacion vacío con estructura anidada
            ObjectNode identificacion = mapper.createObjectNode();
            identificacion.put("numeroIdentificacion", "");
            identificacion.put("tipoIdentificacion", "");
            errorResponse.set("identificacion", identificacion);
            
            // Resto de campos en blanco
            errorResponse.put("nombres", "");
            errorResponse.put("primerApellido", "");
            errorResponse.put("segundoApellido", "");
            errorResponse.put("fechaNacimiento", "0001-01-01T00:00:00");
            errorResponse.put("lugarNacimiento", "");
            errorResponse.put("cedulaVieja", "");
            errorResponse.put("sexo", "");
            errorResponse.put("estadoCivil", "");
            errorResponse.put("categoria", "");
            errorResponse.put("causaInhabilidad", "");
            errorResponse.put("codigoCausaCancelacion", "");
            errorResponse.put("estatus", "");
            errorResponse.put("fechaCancelacion", "0001-01-01T00:00:00");
            errorResponse.put("fotoUrl", "");
            errorResponse.put("fotoBinario", "");
            errorResponse.put("error", mensaje);
            errorResponse.put("errorCode", backendHttpCode);

            exchange.getIn().setBody(errorResponse);
        }

        // *** CONFIGURAR CÓDIGO HTTP DE RESPUESTA ***
        
        // Headers principales de Camel
        exchange.getIn().setHeader("CamelHttpResponseCode", backendHttpCode);
        exchange.getIn().setHeader("CamelHttpResponseText", getHttpStatusText(backendHttpCode));
        exchange.getIn().setHeader("Content-Type", "application/json");
        
        // Headers específicos para Servlet
        exchange.getIn().setHeader("HTTP_RESPONSE_CODE", backendHttpCode);
        
        // Configurar en el message de salida también
        exchange.getOut().setHeader("CamelHttpResponseCode", backendHttpCode);
        exchange.getOut().setHeader("CamelHttpResponseText", getHttpStatusText(backendHttpCode));
        exchange.getOut().setHeader("Content-Type", "application/json");
        exchange.getOut().setBody(exchange.getIn().getBody());
        
        // Intentar configurar en HttpServletResponse si está disponible
        try {
            HttpServletResponse httpResponse = 
                exchange.getIn().getHeader("CamelHttpServletResponse", HttpServletResponse.class);
            if (httpResponse != null) {
                httpResponse.setStatus(backendHttpCode);
                httpResponse.setContentType("application/json");
                log.info("Código HTTP configurado directamente en HttpServletResponse: {}", backendHttpCode);
            }
        } catch (Exception e) {
            log.debug("No se pudo acceder a HttpServletResponse: {}", e.getMessage());
        }

        // Preservar sessionId si existe
        String sessionId = getHeaderSafely(exchange, "sessionId");
        if (sessionId != null) {
            exchange.getIn().setHeader("sessionId", sessionId);
        }

        log.info("Respuesta de error JSON MICM generada con type safety - Código: {}, Mensaje: {}", backendHttpCode, mensaje);
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