package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para validar requests de ConsultarDatosGeneralesCliente MICM.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ValidateConsultarDatosGeneralesClienteRequestProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ValidateConsultarDatosGeneralesClienteRequestProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Validando request ConsultarDatosGeneralesCliente MICM");

        String requestBody = exchange.getIn().getBody(String.class);
        
        if (requestBody == null || requestBody.trim().isEmpty()) {
            throw new ValidationException("Request body no puede ser nulo o vacío");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestJson = mapper.readTree(requestBody);

        // Validar campos requeridos
        String identificacion = requestJson.path("identificacion").asText();
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new ValidationException("Identificación es requerida");
        }

        String tipoIdentificacion = requestJson.path("tipoIdentificacion").asText();
        if (tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) {
            throw new ValidationException("Tipo de identificación es requerido");
        }

        // Validar identificación
        validateIdentification(identificacion, tipoIdentificacion);

        // Obtener valores de campos opcionales
        String forzarActualizar = requestJson.path("forzarActualizar").asText("FALSE");
        String incluirFotoBinaria = requestJson.path("incluirFotoBinaria").asText("FALSE");

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
     * Valida el número y tipo de identificación según las reglas de negocio.
     * 
     * @param identificacion número de identificación
     * @param tipoIdentificacion tipo de identificación
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

        // Validar tipos de identificación permitidos
        if (!"Cedula".equals(tipoIdentificacion) && !"RNC".equals(tipoIdentificacion) && !"Pasaporte".equals(tipoIdentificacion)) {
            throw new ValidationException("Tipo de identificación debe ser: Cedula, RNC o Pasaporte");
        }

        log.debug("Identificación validada exitosamente: {} - {}", identificacion, tipoIdentificacion);
    }
}