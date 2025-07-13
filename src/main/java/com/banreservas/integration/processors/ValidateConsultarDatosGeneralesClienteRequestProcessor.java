package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.inbound.ConsultarDatosGeneralesClienteInboundRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para validar requests de ConsultarDatosGeneralesCliente MICM.
 * Ahora usa type safety con la clase inbound.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ValidateConsultarDatosGeneralesClienteRequestProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ValidateConsultarDatosGeneralesClienteRequestProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Validando request ConsultarDatosGeneralesCliente MICM con type safety");

        String requestBody = exchange.getIn().getBody(String.class);
        
        if (requestBody == null || requestBody.trim().isEmpty()) {
            throw new ValidationException("Request body no puede ser nulo o vacío");
        }

        // Deserializar a objeto type-safe
        ConsultarDatosGeneralesClienteInboundRequest request;
        try {
            request = mapper.readValue(requestBody, ConsultarDatosGeneralesClienteInboundRequest.class);
        } catch (Exception e) {
            throw new ValidationException("Formato JSON inválido: " + e.getMessage());
        }

        // Validar campos requeridos usando el objeto type-safe
        String identificacion = request.identificacion();
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new ValidationException("Identificación es requerida");
        }

        String tipoIdentificacion = request.tipoIdentificacion();
        if (tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) {
            throw new ValidationException("Tipo de identificación es requerido");
        }

        // *** NORMALIZAR TIPO DE IDENTIFICACIÓN (case-insensitive) ***
        tipoIdentificacion = normalizeTipoIdentificacion(tipoIdentificacion);
        if (tipoIdentificacion == null) {
            throw new ValidationException("Tipo de identificación debe ser: Cedula, RNC");
        }

        // Validar identificación
        validateIdentification(identificacion, tipoIdentificacion);

        // Obtener valores de campos opcionales con defaults
        String forzarActualizar = request.forzarActualizar() != null ? request.forzarActualizar() : "FALSE";
        String incluirFotoBinaria = request.incluirFotoBinaria() != null ? request.incluirFotoBinaria() : "FALSE";

        // Validar valores de campos booleanos
        if (!forzarActualizar.equals("TRUE") && !forzarActualizar.equals("FALSE")) {
            throw new ValidationException("forzarActualizar debe ser TRUE o FALSE");
        }

        if (!incluirFotoBinaria.equals("TRUE") && !incluirFotoBinaria.equals("FALSE")) {
            throw new ValidationException("incluirFotoBinaria debe ser TRUE o FALSE");
        }

        // Guardar datos validados en properties del exchange
        exchange.setProperty("identificacionRq", identificacion);
        exchange.setProperty("tipoIdentificacionRq", tipoIdentificacion);
        exchange.setProperty("forzarActualizarRq", forzarActualizar);
        exchange.setProperty("incluirFotoBinariaRq", incluirFotoBinaria);

        // Guardar el objeto completo para referencia futura
        exchange.setProperty("inboundRequest", request);

        // Guardar headers del request original
        exchange.setProperty("canalRq", exchange.getIn().getHeader("Canal"));
        exchange.setProperty("usuarioRq", exchange.getIn().getHeader("Usuario"));
        exchange.setProperty("terminalRq", exchange.getIn().getHeader("Terminal"));
        exchange.setProperty("fechaHoraRq", exchange.getIn().getHeader("FechaHora"));
        exchange.setProperty("versionRq", exchange.getIn().getHeader("Version"));
        exchange.setProperty("servicioRq", exchange.getIn().getHeader("Servicio"));

        exchange.setProperty("originalSessionId", exchange.getIn().getHeader("sessionId"));

        log.info("Validación exitosa para identificación: {} de tipo: {}", identificacion, tipoIdentificacion);
    }

    /**
     * Normaliza el tipo de identificación de forma case-insensitive.
     * 
     * @param tipoIdentificacion tipo de identificación original
     * @return tipo normalizado o null si es inválido
     */
    private String normalizeTipoIdentificacion(String tipoIdentificacion) {
        if (tipoIdentificacion == null) return null;
        
        String tipo = tipoIdentificacion.trim();
        
        // Case-insensitive validation
        if ("cedula".equalsIgnoreCase(tipo)) return "Cedula";
        if ("rnc".equalsIgnoreCase(tipo)) return "RNC";
        
        return null; // Tipo inválido
    }

    /**
     * Valida el número y tipo de identificación según las reglas de negocio.
     * 
     * @param identificacion número de identificación
     * @param tipoIdentificacion tipo de identificación (ya normalizado)
     * @throws ValidationException si la validación falla
     */
    private void validateIdentification(String identificacion, String tipoIdentificacion) throws ValidationException {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new ValidationException("Número de identificación es requerido");
        }

        identificacion = identificacion.trim();

        // Validar que solo contenga dígitos para cédula
        if ("Cedula".equals(tipoIdentificacion)) {
            if (!identificacion.matches("^[0-9]+$")) {
                throw new ValidationException("Número de cédula debe contener solo dígitos numéricos");
            }
            
            if (identificacion.length() != 11) {
                throw new ValidationException("Número de cédula debe tener exactamente 11 dígitos");
            }
        }

        // Validar RNC
        if ("RNC".equals(tipoIdentificacion)) {
            if (!identificacion.matches("^[0-9]+$")) {
                throw new ValidationException("Número de RNC debe contener solo dígitos numéricos");
            }
            
            if (identificacion.length() < 9 || identificacion.length() > 11) {
                throw new ValidationException("Número de RNC debe tener entre 9 y 11 dígitos");
            }
        }

        // Validar tipos de identificación permitidos (ya normalizado)
        if (!"Cedula".equals(tipoIdentificacion) && !"RNC".equals(tipoIdentificacion)) {
            throw new ValidationException("Tipo de identificación debe ser: Cedula, RNC");
        }

        log.debug("Identificación validada exitosamente: {} - {}", identificacion, tipoIdentificacion);
    }
}