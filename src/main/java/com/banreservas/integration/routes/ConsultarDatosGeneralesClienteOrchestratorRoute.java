package com.banreservas.integration.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.request.ConsultarDatosGeneralesClienteRequest;
import com.banreservas.integration.processors.ErrorResponseProcessor;
import com.banreservas.integration.util.Constants;

/**
 * Main orchestrator route for ConsultarDatosGeneralesCliente service.
 * 
 * This route handles the complete orchestration flow for client data consultation,
 * including conditional service calls based on identification type and business rules.
 * 
 * Flow:
 * 1. Receives JSON request via REST endpoint
 * 2. Validates request and determines which services to call
 * 3. Orchestrates calls to external services based on business logic
 * 4. Returns unified response with client data or error information
 * 
 * Business Rules:
 * - RNC identification -> Call juridical client service
 * - Cedula identification -> Call master cedula service first
 * - If master service returns 904 (not found) -> Call JCE service
 * - If force update is TRUE -> Call JCE service and update master data
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class ConsultarDatosGeneralesClienteOrchestratorRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ConsultarDatosGeneralesClienteOrchestratorRoute.class);

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // Global exception handling
        onException(IllegalArgumentException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Validation error in orchestrator: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                .setProperty("errorMessage", simple("${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Unexpected error in orchestrator: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // REST configuration
        restConfiguration()
                .component("platform-http")
                .contextPath("consultar/datos/generales/cliente")
                .bindingMode(RestBindingMode.json)
                .apiProperty("api.title", "Consultar Datos Generales Cliente MICM API")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true")
                .apiProperty("prettyPrint", "true");

        // REST endpoint definition
        rest("/api/v1")
                .post("/consultar-datos-generales-cliente")
                .type(ConsultarDatosGeneralesClienteRequest.class)
                .to("direct:orchestrate-consultar-datos-generales-cliente");

        // Main orchestration route
        from("direct:orchestrate-consultar-datos-generales-cliente")
                .routeId("consultar-datos-generales-cliente-orchestrator")
                .log(LoggingLevel.INFO, logger, "Starting orchestration for ConsultarDatosGeneralesCliente")
                
                // Validate request body
                .choice()
                .when(body().isNull())
                    .log(LoggingLevel.WARN, logger, "Empty request body received")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant(Constants.VALIDATION_MESSAGE_IDENTIFICATION_REQUIRED))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end()
                
                // Store main request and extract headers
                .setProperty("mainRequest", body())
                .setProperty("canalRq", header(Constants.HEADER_CANAL))
                .setProperty("usuarioRq", header(Constants.HEADER_USUARIO))
                .setProperty("terminalRq", header(Constants.HEADER_TERMINAL))
                .setProperty("fechaHoraRq", header(Constants.HEADER_FECHA_HORA))
                .setProperty("versionRq", header(Constants.HEADER_VERSION))
                .setProperty("sessionIdRq", header(Constants.HEADER_SESSION_ID))
                
                // Extract request parameters for business logic
                .setProperty("identificationType", simple("${body.identificationType}"))
                .setProperty("forceUpdate", simple("${body.forceUpdate}"))
                .setProperty("includeBinaryPhoto", simple("${body.includeBinaryPhoto}"))
                
                .log(LoggingLevel.INFO, logger, "Processing request - ID: ${body.identification}, Type: ${body.identificationType}, ForceUpdate: ${body.forceUpdate}")
                
                // Route based on identification type
                .choice()
                
                // Route 1: RNC identification -> Call juridical client service
                .when(simple("${exchangeProperty.identificationType} == '" + Constants.IDENTIFICATION_TYPE_RNC + "'"))
                    .log(LoggingLevel.INFO, logger, "Routing to juridical client service for RNC")
                    .to("direct:call-service1-juridical-client")
                
                // Route 2: Cedula identification -> Start with master cedula service
                .when(simple("${exchangeProperty.identificationType} == '" + Constants.IDENTIFICATION_TYPE_CEDULA + "'"))
                    .log(LoggingLevel.INFO, logger, "Routing to master cedula service for Cedula")
                    .to("direct:call-service2-master-cedula")
                
                // Route 3: Passport identification -> Call juridical client service (if supported)
                .when(simple("${exchangeProperty.identificationType} == '" + Constants.IDENTIFICATION_TYPE_PASSPORT + "'"))
                    .log(LoggingLevel.INFO, logger, "Routing to juridical client service for Passport")
                    .to("direct:call-service1-juridical-client")
                
                // Invalid identification type
                .otherwise()
                    .log(LoggingLevel.WARN, logger, "Invalid identification type: ${exchangeProperty.identificationType}")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant(Constants.VALIDATION_MESSAGE_INVALID_IDENTIFICATION_TYPE))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end()
                
                .log(LoggingLevel.INFO, logger, "Orchestration completed for request ID: ${exchangeProperty.mainRequest.identification}")
                .end();
    }
}
