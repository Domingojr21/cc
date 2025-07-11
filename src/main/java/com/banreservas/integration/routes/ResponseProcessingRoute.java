package com.banreservas.integration.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.response.BodyDto;
import com.banreservas.integration.model.outbound.response.ClientDto;
import com.banreservas.integration.model.outbound.response.ConsultarDatosGeneralesClienteResponse;
import com.banreservas.integration.model.outbound.response.HeaderDto;
import com.banreservas.integration.model.outbound.response.IdentificationDto;
import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.ActualizarDatosMaestroCeduladosResponse;
import com.banreservas.integration.model.outbound.response.backends.actualizardatosmaestrocedulados.ClientActualizarResponseDto;
import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.ClientMaestroResponseDto;
import com.banreservas.integration.model.outbound.response.backends.datosmaestrocedulados.ConsultarDatosMaestroCeduladosResponse;
import com.banreservas.integration.processors.ErrorResponseProcessor;
import com.banreservas.integration.util.Constants;

/**
 * Route for processing final responses from master cedula and update services.
 * 
 * This route handles the final response generation for Cedula identification types.
 * It maps the response data from either master cedula service or update service
 * to the unified response format.
 * 
 * Flow:
 * 1. Determine which service response to process (master or update)
 * 2. Map service-specific response to unified format
 * 3. Generate final JSON response
 * 
 * @author Integration Team
 * @version 1.0
 */
@ApplicationScoped
public class ResponseProcessingRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ResponseProcessingRoute.class);

    @Inject
    ErrorResponseProcessor errorResponseProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error processing final response: ${exception.message}")
                .setProperty("errorCode", constant(Constants.HTTP_INTERNAL_ERROR))
                .setProperty("errorMessage", simple("Error processing final response: ${exception.message}"))
                .process(errorResponseProcessor)
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("Content-Type", constant("application/json; charset=UTF-8"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .end();

        from("direct:process-master-response")
                .routeId("process-master-response")
                .log(LoggingLevel.INFO, logger, "Processing master cedula response")
                
                .process(exchange -> {
                    ConsultarDatosMaestroCeduladosResponse masterResponse = 
                        (ConsultarDatosMaestroCeduladosResponse) exchange.getProperty("service2Response");
                    
                    logger.info("Retrieved masterResponse from exchange property: {}", masterResponse != null ? "NOT NULL" : "NULL");
                    
                    if (masterResponse == null || masterResponse.body() == null || 
                        masterResponse.body().clients() == null || masterResponse.body().clients().isEmpty()) {
                        logger.error("Invalid master cedula response - masterResponse: {}, body: {}, clients: {}", 
                                   masterResponse != null ? "EXISTS" : "NULL",
                                   masterResponse != null && masterResponse.body() != null ? "EXISTS" : "NULL",
                                   masterResponse != null && masterResponse.body() != null && masterResponse.body().clients() != null ? 
                                       masterResponse.body().clients().size() : "NULL");
                        throw new IllegalArgumentException("Invalid master cedula response");
                    }
                    
                    ClientMaestroResponseDto masterClient = masterResponse.body().clients().get(0);
                    logger.info("Processing client: {}, identifications: {}", 
                               masterClient.names(), 
                               masterClient.identifications() != null ? masterClient.identifications().size() : "NULL");
                    
                    // Validar que existen identificaciones
                    if (masterClient.identifications() == null || masterClient.identifications().isEmpty()) {
                        logger.error("Client has no identifications");
                        throw new IllegalArgumentException("Client has no identifications");
                    }
                    
                    // Map to unified response format
                    IdentificationDto identification = new IdentificationDto(
                        masterClient.identifications().get(0).number(),
                        masterClient.identifications().get(0).type()
                    );
                    
                    // For cedula clients, use names as business name and empty trade name
                    ClientDto client = new ClientDto(
                        identification,
                        masterClient.names() != null ? masterClient.names() : "", // businessName
                        "" // tradeName (empty for cedula)
                    );
                    
                    BodyDto body = new BodyDto(client);
                    HeaderDto header = new HeaderDto(Constants.HTTP_OK, Constants.RESPONSE_MESSAGE_SUCCESS);
                    
                    ConsultarDatosGeneralesClienteResponse finalResponse = 
                        new ConsultarDatosGeneralesClienteResponse(header, body);
                    
                    logger.info("Final response created successfully - Header: {}, Body: {}", 
                               finalResponse.header().responseCode(), 
                               finalResponse.body() != null ? "EXISTS" : "NULL");
                    
                    exchange.getIn().setBody(finalResponse);
                    
                    logger.info("Master cedula response processed successfully for ID: {}", 
                               masterClient.identifications().get(0).number());
                })
                
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("Content-Type", constant("application/json; charset=UTF-8"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .log(LoggingLevel.INFO, logger, "Final JSON response: ${body}")
                .end();

        from("direct:process-update-response")
                .routeId("process-update-response")
                .log(LoggingLevel.INFO, logger, "Processing update service response")
                
                .process(exchange -> {
                    ActualizarDatosMaestroCeduladosResponse updateResponse = 
                        (ActualizarDatosMaestroCeduladosResponse) exchange.getProperty("service4Response");
                    
                    if (updateResponse == null || updateResponse.body() == null || 
                        updateResponse.body().clients() == null || updateResponse.body().clients().isEmpty()) {
                        throw new IllegalArgumentException("Invalid update service response");
                    }
                    
                    ClientActualizarResponseDto updateClient = updateResponse.body().clients().get(0);
                    
                    // Map to unified response format
                    IdentificationDto identification = new IdentificationDto(
                        updateClient.identifications().get(0).number(),
                        updateClient.identifications().get(0).type()
                    );
                    
                    // For updated cedula clients, use name as business name
                    ClientDto client = new ClientDto(
                        identification,
                        updateClient.name() != null ? updateClient.name() : "", // businessName
                        "" // tradeName (empty for cedula)
                    );
                    
                    BodyDto body = new BodyDto(client);
                    HeaderDto header = new HeaderDto(Constants.HTTP_OK, Constants.RESPONSE_MESSAGE_SUCCESS);
                    
                    ConsultarDatosGeneralesClienteResponse finalResponse = 
                        new ConsultarDatosGeneralesClienteResponse(header, body);
                    
                    exchange.getIn().setBody(finalResponse);
                    
                    logger.info("Update service response processed successfully for ID: {}", 
                               updateClient.identifications().get(0).number());
                })
                
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("Content-Type", constant("application/json; charset=UTF-8"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .log(LoggingLevel.INFO, logger, "Final JSON response: ${body}")
                .end();
    }
}