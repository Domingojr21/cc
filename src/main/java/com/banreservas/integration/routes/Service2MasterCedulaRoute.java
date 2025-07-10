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
import com.banreservas.integration.processors.GenerateService2RequestProcessor;
import com.banreservas.integration.processors.ProcessService2ResponseProcessor;
import com.banreservas.integration.util.Constants;

import java.net.SocketTimeoutException;

/**
 * Route for calling ConsultarDatosMaestroCedulados service (Service 2).
 * 
 * This route handles calls to the master cedula service for Cedula identification
 * types when force update is FALSE. Based on the response, it determines if
 * the JCE service should be called.
 * 
 * Service Expression: Body.Clientes.Cliente.Identificaciones.Identificacion.Tipo = 'Cedula' 
 *                    AND Options.ForzarActualizar=FALSE
 * 
 * Flow:
 * 1. Generate request for master cedula service
 * 2. Call external service
 * 3. Process response and check for error code 904 (not found)
 * 4. If 904 or force update = TRUE, route to JCE service
 * 5. Otherwise, return master data response
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class Service2MasterCedulaRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Service2MasterCedulaRoute.class);

    @ConfigProperty(name = "consultar.datos.maestro.cedulados.url")
    String service2Url;

    @ConfigProperty(name = "timeout.consultar.datos.maestro.cedulados")
    String service2Timeout;

    @Inject
    GenerateService2RequestProcessor generateService2RequestProcessor;

    @Inject
    ProcessService2ResponseProcessor processService2ResponseProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // Timeout exception handling
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout calling master cedula service")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", constant("Timeout connecting to master cedula service"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // General exception handling
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error calling master cedula service: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("Error in master cedula service: ${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Service 2 call route
        from("direct:call-service2-master-cedula")
                .routeId("service2-master-cedula-call")
                .log(LoggingLevel.INFO, logger, "Calling ConsultarDatosMaestroCedulados service")

                // Generate request for service 2
                .process(generateService2RequestProcessor)
                .marshal().json(JsonLibrary.Jackson)

                // Set HTTP headers
                .setHeader(Constants.HEADER_SESSION_ID, exchangeProperty("sessionIdRq"))
                .setHeader(Constants.HEADER_CONTENT_TYPE, constant("application/json"))
                .setHeader(Constants.HEADER_AUTHORIZATION, header(Constants.HEADER_AUTHORIZATION))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))

                // Call external service
                .toD(service2Url + "?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClientConfigurer=#selfSignedHttpClientConfigurer")

                // Handle response based on HTTP status code
                .choice()

                // 200 - Success (but need to check body code)
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Master cedula service returned success - HTTP 200")
                    .process(processService2ResponseProcessor)
                    
                    // Check if JCE service should be called
                    .choice()
                    .when(exchangeProperty("callJCEService").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Client not found in master data or force update requested - Calling JCE service")
                        .to("direct:call-service3-jce")
                    .when(exchangeProperty("clientFoundInMaster").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Client found in master data - Preparing final response")
                        .to("direct:process-master-response")
                    .otherwise()
                        .log(LoggingLevel.ERROR, logger, "Unexpected condition after master cedula service call")
                        .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                        .setProperty("errorMessage", constant("Unexpected condition in master cedula processing"))
                        .process(errorResponseProcessor)
                        .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 400 - Bad Request
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Master cedula service returned bad request - HTTP 400")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant("Invalid request to master cedula service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 401 - Unauthorized
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Master cedula service returned unauthorized - HTTP 401")
                    .setProperty("errorCode", constant(401))
                    .setProperty("errorMessage", constant("Unauthorized access to master cedula service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 500 - Internal Server Error
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Master cedula service returned internal error - HTTP 500")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", constant("Internal error in master cedula service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // Other error codes
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Master cedula service returned unexpected code - HTTP ${header.CamelHttpResponseCode}")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", simple("Unexpected error from master cedula service: HTTP ${header.CamelHttpResponseCode}"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}