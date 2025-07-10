package com.banreservas.integration.processors;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.ConsultarDatosGeneralesClienteResponse;
import com.banreservas.integration.model.outbound.response.HeaderDto;
import com.banreservas.integration.util.Constants;

/**
 * Processor to handle errors and generate error responses.
 * Centralizes error handling for all services in the orchestration.
 */
@ApplicationScoped
public class ErrorResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de error");

        // Obtener información del error
        Integer errorCode = (Integer) exchange.getProperty("errorCode");
        String errorMessage = (String) exchange.getProperty("errorMessage");
        
        // Valores por defecto si no se especifican
        if (errorCode == null) {
            errorCode = Constants.HTTP_INTERNAL_ERROR;
        }
        
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = getDefaultErrorMessage(errorCode);
        }

        // Crear respuesta de error
        HeaderDto errorHeader = new HeaderDto(errorCode, errorMessage);
        ConsultarDatosGeneralesClienteResponse errorResponse = 
            new ConsultarDatosGeneralesClienteResponse(errorHeader, null);

        // Establecer código HTTP y cuerpo de respuesta
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, errorCode);
        exchange.getIn().setBody(errorResponse);

        logger.error("Respuesta de error generada: HTTP {} - {}", errorCode, errorMessage);
    }

    private String getDefaultErrorMessage(Integer errorCode) {
        return switch (errorCode) {
            case Constants.HTTP_BAD_REQUEST -> Constants.VALIDATION_MESSAGE_IDENTIFICATION_REQUIRED;
            case Constants.HTTP_INTERNAL_ERROR -> Constants.ERROR_MESSAGE_INTERNAL_ERROR;
            default -> Constants.ERROR_MESSAGE_SERVICE_UNAVAILABLE;
        };
    }
}