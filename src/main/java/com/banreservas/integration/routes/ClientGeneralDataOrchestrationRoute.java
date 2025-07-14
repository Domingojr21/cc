package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.processors.BackendRequestGenerationProcessor;
import com.banreservas.integration.processors.BackendResponseMappingProcessor;
import com.banreservas.integration.processors.ErrorResponseProcessorMICM;
import com.banreservas.integration.processors.OrchestrationDecisionProcessor;
import com.banreservas.integration.processors.RequestValidationProcessor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.SocketTimeoutException;

/**
 * Ruta principal de orquestación para consulta de datos generales de cliente MICM.
 * Implementa el flujo completo desde la validación hasta la respuesta final,
 * coordinando múltiples servicios backend según las reglas de negocio.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 11/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class ClientGeneralDataOrchestrationRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ClientGeneralDataOrchestrationRoute.class);
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("ms-orq-consultar-datos-generales-cliente-micm");

    @Inject RequestValidationProcessor requestValidationProcessor;
    @Inject OrchestrationDecisionProcessor orchestrationDecisionProcessor;
    @Inject BackendRequestGenerationProcessor backendRequestGenerationProcessor;
    @Inject BackendResponseMappingProcessor backendResponseMappingProcessor;
    @Inject ErrorResponseProcessorMICM errorResponseProcessor;

    @Override
    public void configure() throws Exception {
        configureExceptionHandling();
        configureMainRoute();
        configureJceExecutionFlow();
    }

    /**
     * Configura el manejo global de excepciones.
     */
    private void configureExceptionHandling() {
        // Manejo de errores de validación
        onException(ValidationException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Error de validación: ${exception.message}")
                .process(exchange -> {
                    Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                    exchange.setProperty("Mensaje", exception.getMessage());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                })
                .process(errorResponseProcessor)
                .log(LoggingLevel.INFO, AUDIT_LOGGER,
                    "sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | " +
                    "response=${body} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de timeout
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Timeout conectando a servicios backend")
                .process(exchange -> {
                    exchange.setProperty("Mensaje", "Timeout al conectar con servicios backend");
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
                })
                .process(errorResponseProcessor)
                .log(LoggingLevel.INFO, AUDIT_LOGGER,
                    "sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | " +
                    "response=${body} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo de excepciones generales
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado: ${exception.message}")
                .process(exchange -> {
                    String errorMessage = determineErrorMessage(exchange);
                    Integer httpCode = determineHttpCode(exchange);
                    
                    exchange.setProperty("Mensaje", errorMessage);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);
                })
                .process(errorResponseProcessor)
                .log(LoggingLevel.INFO, AUDIT_LOGGER,
                    "sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | " +
                    "response=${body} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .end();
    }

    /**
     * Configura la ruta principal de orquestación.
     */
    private void configureMainRoute() {
        from("platform-http:/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente?httpMethodRestrict=POST")
                .routeId("client-general-data-orchestration-route")
                .log(LoggingLevel.INFO, logger, "Solicitud de consulta de datos generales recibida")
                
                // Auditoría inicial - capturar request original
                .process(exchange -> {
                    String requestBody = exchange.getIn().getBody(String.class);
                    exchange.setProperty("requestAudit", requestBody != null ? requestBody : "");
                })
                
                // Validación y preparación
                .process(requestValidationProcessor)
                .process(orchestrationDecisionProcessor)
                .process(backendRequestGenerationProcessor)

                // Ejecución de servicios
                .to("direct:execute-backend-services")

                // Procesamiento de respuesta exitosa
                .process(backendResponseMappingProcessor)
                .process(exchange -> {
                    exchange.setProperty("Codigo", "200");
                    exchange.setProperty("Mensaje", "Consulta procesada exitosamente");
                })

                // Finalización
                .setHeader("sessionId", exchangeProperty("originalSessionId"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log(LoggingLevel.INFO, AUDIT_LOGGER,
                    "sessionID=${headers.sessionId} | request=${exchangeProperty.requestAudit} | " +
                    "response=${body} | errorCode=${exchangeProperty.Codigo} | errorMessage=${exchangeProperty.Mensaje}")
                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.INFO, logger, "Consulta de datos generales procesada exitosamente");

        // Ruta para ejecución de servicios backend
        from("direct:execute-backend-services")
                .routeId("execute-backend-services-route")
                .choice()
                    .when(exchangeProperty("callLegalClientService").isEqualTo(true))
                        .to("direct:execute-legal-client-service")
                    .when(exchangeProperty("callMasterDataService").isEqualTo(true))
                        .to("direct:execute-master-data-service")
                    .when(exchangeProperty("callJceService").isEqualTo(true))
                        .to("direct:execute-jce-flow")
                .end()
                
                // Verificar errores después de la ejecución
                .choice()
                    .when(exchangeProperty("hasBackendError").isEqualTo(true))
                        .process(exchange -> {
                            String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                            Integer errorCode = exchange.getProperty("backendErrorCode", Integer.class);
                            throw new RuntimeException(errorMsg != null ? errorMsg : "Error en servicio backend");
                        })
                .end()
                
                // Verificar si se debe ejecutar JCE dinámicamente (código 904)
                .choice()
                    .when(exchangeProperty("clientNotFoundInMaster").isEqualTo(true))
                        .log(LoggingLevel.INFO, logger, "Ejecutando flujo JCE dinámico por código 904")
                        .to("direct:execute-jce-flow")
                        .choice()
                            .when(exchangeProperty("hasBackendError").isEqualTo(true))
                                .process(exchange -> {
                                    String errorMsg = exchange.getProperty("backendErrorMessage", String.class);
                                    Integer errorCode = exchange.getProperty("backendErrorCode", Integer.class);
                                    throw new RuntimeException(errorMsg != null ? errorMsg : "Error en flujo JCE dinámico");
                                })
                        .end()
                .end();
    }

    /**
     * Configura el flujo de ejecución JCE secuencial.
     */
    private void configureJceExecutionFlow() {
        from("direct:execute-jce-flow")
                .routeId("jce-execution-flow-route")
                .log(LoggingLevel.INFO, logger, "Iniciando flujo JCE secuencial")
                
                .to("direct:execute-jce-service")
                .to("direct:execute-update-master-service-if-needed")
                
                .log(LoggingLevel.INFO, logger, "Flujo JCE completado");
    }

    /**
     * Determina el mensaje de error apropiado.
     */
    private String determineErrorMessage(Exchange exchange) {
        String backendMessage = (String) exchange.getProperty("backendErrorMessage");
        if (backendMessage != null && !backendMessage.trim().isEmpty()) {
            return backendMessage;
        }
        
        Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        if (exception != null && exception.getMessage() != null) {
            return exception.getMessage();
        }
        
        return "Error interno del servidor";
    }

    /**
     * Determina el código HTTP apropiado.
     */
    private Integer determineHttpCode(Exchange exchange) {
        Integer backendCode = (Integer) exchange.getProperty("backendErrorCode");
        if (backendCode != null) {
            return backendCode;
        }
        
        return 500; // Código por defecto
    }
}