package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.inbound.GetClientGeneralDataInboundRequest;
import com.banreservas.integration.util.BooleanValueUtil;
import com.banreservas.integration.util.Constants;
import com.banreservas.integration.util.IdentificationTypeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para validar requests de ConsultarDatosGeneralesCliente MICM.
 * Ahora usa type safety con la clase inbound.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class ValidateGetClientGeneralDataRequestProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(ValidateGetClientGeneralDataRequestProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        
        String requestBody = exchange.getIn().getBody(String.class);
        
        if (requestBody == null || requestBody.trim().isEmpty()) {
            throw new ValidationException("Request body no puede ser nulo o vacío");
        }

        // Deserializar a objeto 
        GetClientGeneralDataInboundRequest request;
        try {
            request = mapper.readValue(requestBody, GetClientGeneralDataInboundRequest.class);
        } catch (Exception e) {
            throw new ValidationException("Formato JSON inválido: " + e.getMessage());
        }

        // Validar campos requeridos 
        String identificacion = request.indentificationNumber();
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new ValidationException("Identificación es requerida");
        }

        String tipoIdentificacion = request.identificationType();
        if (tipoIdentificacion == null || tipoIdentificacion.trim().isEmpty()) {
            throw new ValidationException("Tipo de identificación es requerido");
        }

        // *** NORMALIZAR TIPO DE IDENTIFICACIÓN (case-insensitive) ***
        String tipoIdentificacionNormalizado = IdentificationTypeUtil.normalize(tipoIdentificacion);
        if (tipoIdentificacionNormalizado == null) {
            throw new ValidationException("Tipo de identificación debe ser: Cedula o RNC");
        }

        // Validar identificación
        validateIdentification(identificacion, tipoIdentificacionNormalizado);

        // *** NORMALIZAR VALORES BOOLEANOS (case-insensitive) ***
        String forzarActualizarOriginal = request.forceUpdate() != null ? request.forceUpdate() : Constants.BOOLEAN_FALSE;
        String forzarActualizar = BooleanValueUtil.normalize(forzarActualizarOriginal);
        if (forzarActualizar == null) {
            throw new ValidationException("forzarActualizar debe ser TRUE o FALSE");
        }

        String incluirFotoBinariaOriginal = request.includeBinaryPhoto() != null ? request.includeBinaryPhoto() : Constants.BOOLEAN_FALSE;
        String incluirFotoBinaria = BooleanValueUtil.normalize(incluirFotoBinariaOriginal);
        if (incluirFotoBinaria == null) {
            throw new ValidationException("incluirFotoBinaria debe ser TRUE o FALSE");
        }

        // Guardar datos validados y normalizados en properties del exchange
        exchange.setProperty("identificacionRq", identificacion);
        exchange.setProperty("tipoIdentificacionRq", tipoIdentificacionNormalizado);
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

        log.info("Validación exitosa para identificación: {} de tipo: {}", identificacion, tipoIdentificacionNormalizado);
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
        if (Constants.IDENTIFICATION_TYPE_CEDULA.equals(tipoIdentificacion)) {
            if (!identificacion.matches("^[0-9]+$")) {
                throw new ValidationException("Número de cédula debe contener solo dígitos numéricos");
            }
            
            if (identificacion.length() != 11) {
                throw new ValidationException("Número de cédula debe tener exactamente 11 dígitos");
            }
        }

        // Validar RNC
        if (Constants.IDENTIFICATION_TYPE_RNC.equals(tipoIdentificacion)) {
            if (!identificacion.matches("^[0-9]+$")) {
                throw new ValidationException("Número de RNC debe contener solo dígitos numéricos");
            }
            
            if (identificacion.length() < 9 || identificacion.length() > 11) {
                throw new ValidationException("Número de RNC debe tener entre 9 y 11 dígitos");
            }
        }

    }
}