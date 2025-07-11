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
import com.banreservas.integration.processors.GenerateService1RequestProcessor;
import com.banreservas.integration.processors.ProcessService1ResponseProcessor;
import com.banreservas.integration.util.Constants;

import java.net.SocketTimeoutException;

/**
 * Route for calling ConsultarDatosGeneralesClienteJuridico service (Service 1).
 * 
 * This route handles calls to the juridical client service for RNC and Passport
 * identification types. It includes proper error handling and response mapping.
 * 
 * Service Expression: Body.Clientes.Cliente.Identificaciones.Identificacion.Tipo = 'RNC'
 * 
 * Flow:
 * 1. Generate request for juridical client service
 * 2. Call external service with proper headers
 * 3. Process response and handle different HTTP status codes
 * 4. Map successful response to final format
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class Service1JuridicalClientRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Service1JuridicalClientRoute.class);

    @ConfigProperty(name = "consultar.datos.generales.cliente.juridico.url")
    String service1Url;

    @ConfigProperty(name = "timeout.consultar.datos.generales.cliente.juridico")
    String service1Timeout;

    @Inject
    GenerateService1RequestProcessor generateService1RequestProcessor;

    @Inject
    ProcessService1ResponseProcessor processService1ResponseProcessor;

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        // Timeout exception handling
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout calling juridical client service")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", constant("Timeout connecting to juridical client service"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // General exception handling
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error calling juridical client service: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("Error in juridical client service: ${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Service 1 call route
        from("direct:call-service1-juridical-client")
                .routeId("service1-juridical-client-call")
                .log(LoggingLevel.INFO, logger, "Calling ConsultarDatosGeneralesClienteJuridico service")

                // Generate request for service 1
                .process(generateService1RequestProcessor)
                .marshal().json(JsonLibrary.Jackson)

                // Set HTTP headers from original request
                .setHeader(Constants.HEADER_SESSION_ID, exchangeProperty("sessionIdRq"))
                .setHeader(Constants.HEADER_CONTENT_TYPE, constant("application/json"))
                .setHeader(Constants.HEADER_AUTHORIZATION, header(Constants.HEADER_AUTHORIZATION))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))

                // Call external service
                .toD(service1Url + "?bridgeEndpoint=true&throwExceptionOnFailure=false&httpClientConfigurer=#selfSignedHttpClientConfigurer")

                // Handle response based on HTTP status code
                .choice()

                // 200 - Success
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
                    .log(LoggingLevel.INFO, logger, "Juridical client service returned success - HTTP 200")
                    .unmarshal().json(JsonLibrary.Jackson)
                    .process(processService1ResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .setHeader("Content-Type", constant("application/json; charset=UTF-8"))
.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                    .stop()

                // 400 - Bad Request
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(400))
                    .log(LoggingLevel.WARN, logger, "Juridical client service returned bad request - HTTP 400")
                    .setProperty("errorCode", constant(Constants.HTTP_BAD_REQUEST))
                    .setProperty("errorMessage", constant("Invalid request to juridical client service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 401 - Unauthorized
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(401))
                    .log(LoggingLevel.WARN, logger, "Juridical client service returned unauthorized - HTTP 401")
                    .setProperty("errorCode", constant(401))
                    .setProperty("errorMessage", constant("Unauthorized access to juridical client service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // 500 - Internal Server Error
                .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(500))
                    .log(LoggingLevel.ERROR, logger, "Juridical client service returned internal error - HTTP 500")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", constant("Internal error in juridical client service"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()

                // Other error codes
                .otherwise()
                    .log(LoggingLevel.ERROR, logger, "Juridical client service returned unexpected code - HTTP ${header.CamelHttpResponseCode}")
                    .setProperty("errorCode", constant(502))
                    .setProperty("errorMessage", simple("Unexpected error from juridical client service: HTTP ${header.CamelHttpResponseCode}"))
                    .process(errorResponseProcessor)
                    .marshal().json(JsonLibrary.Jackson)
                    .stop()
                .end();
    }
}