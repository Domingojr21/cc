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
import com.banreservas.integration.processors.GenerateService3RequestProcessor;
import com.banreservas.integration.processors.ProcessService3ResponseProcessor;
import com.banreservas.integration.util.Constants;

import java.net.SocketTimeoutException;

/**
 * Route for calling ConsultarDatosJCEDP service (Service 3).
 * 
 * This route handles calls to the JCE service when the master cedula service
 * returns error code 904 (not found) or when force update is TRUE.
 * 
 * Service Expression: (Body.Clientes.Cliente.Identificaciones.Identificacion.Tipo = 'Cedula') 
 *                    AND (Code.ConsultarDatosMaestroCedulados_SP = '904' OR Options.ForzarActualizar=TRUE)
 * 
 * Flow:
 * 1. Generate request for JCE service
 * 2. Call external JCE service
 * 3. Process response and determine if master data should be updated
 * 4. If data found in JCE, route to update service
 * 5. Otherwise, return appropriate error response
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class Service3JCERoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Service3JCERoute.class);

    @ConfigProperty(name = "consultar.datos.jcedp.url")
    String service3Url;

    @ConfigProperty(name = "timeout.consultar.datos.jcedp")
    String service3Timeout;

    @Inject
    GenerateService3RequestProcessor generateService3RequestProcessor;

    @Inject
    ProcessService3ResponseProcessor processService3ResponseProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // Timeout exception handling
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout calling JCE service")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", constant("Timeout connecting to JCE service"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // General exception handling
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error calling JCE service: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("Error in JCE service: ${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Service 3 call route
        from("direct:call-service3-jce")
                .routeId("service3-jce-call")
                .log(LoggingLevel.INFO, logger, "Calling ConsultarDatosJCEDP service")

                // Generate request for service 3
                .process(generateService3RequestProcessor)
                .marshal().json(JsonLibrary.Jackson)

                // Set HTTP headers
                .setHeader(Constants.HEADER_SESSION_ID, exchangeProperty("sessionIdRq"))
                .setHeader(Constants.HEADER_CONTENT_TYPE, constant("application/json"))
                .setHeader(Constants.HEADER_AUTHORIZATION, header(Constants.HEADER_AUTHORIZATION))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))

                // Call external service
                .toD(service3Url + "?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClientConfigurer=#selfSignedHttpClientConfigurer")

                // Handle response based on HTTP status code
                .choice()

                // 200 - Success
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "JCE service returned success - HTTP 200")
                    .unmarshal().json(JsonLibrary.Jackson)
                    .process(processService3ResponseProcessor)
                    
                    // Check if update service should be called
                   .choice()
                        .when(exchangeProperty("callUpdateService").isEqualTo(true))
                            .log(LoggingLevel.INFO, logger, "Client found in JCE - Calling update service")
                            .to("direct:call-service4-update")
                        .when(exchangeProperty("clientNotFoundInJCE").isEqualTo(true))
                            .log(LoggingLevel.WARN, logger, "Client not found in JCE - Returning error")
                            .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                            .setProperty("errorMessage", constant(Constants.ERROR_MESSAGE_CLIENT_NOT_FOUND))
                            .process(errorResponseProcessor)
                            .marshal().json(JsonLibrary.Jackson)
                    .endChoice()
                    .stop()

                // 400 - Bad Request
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "JCE service returned bad request - HTTP 400")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant("Invalid request to JCE service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 401 - Unauthorized
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "JCE service returned unauthorized - HTTP 401")
                    .setProperty("errorCode", constant(401))
                    .setProperty("errorMessage", constant("Unauthorized access to JCE service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 500 - Internal Server Error
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "JCE service returned internal error - HTTP 500")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", constant("Internal error in JCE service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // Other error codes
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "JCE service returned unexpected code - HTTP ${header.CamelHttpResponseCode}")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", simple("Unexpected error from JCE service: HTTP ${header.CamelHttpResponseCode}"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}