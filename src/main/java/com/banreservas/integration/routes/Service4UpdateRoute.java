package com.banreservas.integration.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.processors.ErrorResponseProcessor;
import com.banreservas.integration.processors.GenerateService4RequestProcessor;
import com.banreservas.integration.processors.ProcessService4ResponseProcessor;
import com.banreservas.integration.util.Constants;

import java.net.SocketTimeoutException;

/**
 * Route for calling ActualizarDatosMaestroCedulados service (Service 4).
 * 
 * This route handles calls to the update master cedula service when JCE service
 * returns valid client data. It updates the master database with current JCE information.
 * 
 * Service Expression: Body.Clientes.Cliente.Identificaciones.Identificacion.Tipo = 'Cedula' 
 *                    AND (Options.ForzarActualizar=TRUE)
 * 
 * Flow:
 * 1. Generate update request using JCE service response data
 * 2. Call external update service
 * 3. Process response and validate update success
 * 4. Return updated client data or error response
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class Service4UpdateRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Service4UpdateRoute.class);

    @ConfigProperty(name = "actualizar.datos.maestro.cedulados.url")
    String service4Url;

    @ConfigProperty(name = "timeout.actualizar.datos.maestro.cedulados")
    String service4Timeout;

    @Inject
    GenerateService4RequestProcessor generateService4RequestProcessor;

    @Inject
    ProcessService4ResponseProcessor processService4ResponseProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // Timeout exception handling
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout calling update service")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", constant("Timeout connecting to update service"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // General exception handling
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error calling update service: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("Error in update service: ${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Service 4 call route
        from("direct:call-service4-update")
                .routeId("service4-update-call")
                .log(LoggingLevel.INFO, logger, "Calling ActualizarDatosMaestroCedulados service")

                // Generate request for service 4
                .process(generateService4RequestProcessor)
                .marshal().json(JsonLibrary.Jackson)

                // Set HTTP headers
                .setHeader(Constants.HEADER_SESSION_ID, exchangeProperty("sessionIdRq"))
                .setHeader(Constants.HEADER_CONTENT_TYPE, constant("application/json"))
                .setHeader(Constants.HEADER_AUTHORIZATION, header(Constants.HEADER_AUTHORIZATION))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))

                // Call external service
                .toD(service4Url + "?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClientConfigurer=#selfSignedHttpClientConfigurer")

                // Handle response based on HTTP status code
                .choice()

                // 200 - Success
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Update service returned success - HTTP 200")
                    .unmarshal().json(JsonLibrary.Jackson)
                    .process(processService4ResponseProcessor)
                    
                    // Check if update was successful
                    .choice()
                    .when(exchangeProperty("dataUpdatedSuccessfully").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Data updated successfully - Preparing final response")
                        .to("direct:process-update-response")
                    .otherwise()
                        .log(LoggingLevel.ERROR, logger, "Update failed - Processing error")
                        .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                        .setProperty("errorMessage", constant("Failed to update master data"))
                        .process(errorResponseProcessor)
                        .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 400 - Bad Request
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Update service returned bad request - HTTP 400")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant("Invalid request to update service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 401 - Unauthorized
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Update service returned unauthorized - HTTP 401")
                    .setProperty("errorCode", constant(401))
                    .setProperty("errorMessage", constant("Unauthorized access to update service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 500 - Internal Server Error
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Update service returned internal error - HTTP 500")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", constant("Internal error in update service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // Other error codes
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Update service returned unexpected code - HTTP ${header.CamelHttpResponseCode}")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", simple("Unexpected error from update service: HTTP ${header.CamelHttpResponseCode}"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}