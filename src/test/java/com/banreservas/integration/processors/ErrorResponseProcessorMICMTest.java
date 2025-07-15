package com.banreservas.integration.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ErrorResponseProcessorMICMTest {

    private ErrorResponseProcessorMICM processor;
    private Exchange exchange;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        processor = new ErrorResponseProcessorMICM();
        exchange = new DefaultExchange(new DefaultCamelContext());
        mapper = new ObjectMapper();
    }

    @Test
    void shouldGenerateErrorResponseFromBackendErrorCode() throws Exception {
        exchange.setProperty("backendErrorCode", "404");
        exchange.setProperty("backendErrorMessage", "Cliente no encontrado");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals(404, response.get("codigo").asInt());
        assertEquals("Cliente no encontrado", response.get("mensaje").asText());
        assertEquals(404, exchange.getIn().getHeader("CamelHttpResponseCode"));
    }

    @Test
    void shouldGenerateErrorResponseFromHttpHeader() throws Exception {
        exchange.getIn().setHeader("CamelHttpResponseCode", 500);
        exchange.setProperty("Mensaje", "Error interno del servidor");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals(500, response.get("codigo").asInt());
        assertEquals("Error interno del servidor", response.get("mensaje").asText());
    }

    @Test
    void shouldUseDefaultValuesWhenNoErrorInfoProvided() throws Exception {
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals(500, response.get("codigo").asInt());
        assertEquals("Error interno del servidor", response.get("mensaje").asText());
        assertEquals(500, exchange.getIn().getHeader("CamelHttpResponseCode"));
    }

    @Test
    void shouldPreferBackendErrorCodeOverHttpHeader() throws Exception {
        exchange.setProperty("backendErrorCode", "404");
        exchange.setProperty("backendErrorMessage", "Backend error");
        exchange.getIn().setHeader("CamelHttpResponseCode", 500);
        exchange.setProperty("Mensaje", "Header error");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals(404, response.get("codigo").asInt());
        assertEquals("Backend error", response.get("mensaje").asText());
    }

    @Test
    void shouldPreferBackendErrorMessageOverPropertyMessage() throws Exception {
        exchange.setProperty("backendErrorMessage", "Backend specific error");
        exchange.setProperty("Mensaje", "Generic error message");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals("Backend specific error", response.get("mensaje").asText());
    }

    @Test
    void shouldHandleInvalidBackendErrorCode() throws Exception {
        exchange.setProperty("backendErrorCode", "invalid_code");
        exchange.setProperty("backendErrorMessage", "Test error");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals(500, response.get("codigo").asInt());
        assertEquals("Test error", response.get("mensaje").asText());
    }

    @Test
    void shouldSetCorrectContentTypeHeader() throws Exception {
        processor.process(exchange);
        
        assertEquals("application/json", exchange.getIn().getHeader("Content-Type"));
        assertEquals("application/json", exchange.getMessage().getHeader("Content-Type"));
    }

    @Test
    void shouldSetCorrectHttpStatusText() throws Exception {
        exchange.setProperty("backendErrorCode", "404");
        
        processor.process(exchange);
        
        assertEquals("Not Found", exchange.getIn().getHeader("CamelHttpResponseText"));
        assertEquals("Not Found", exchange.getMessage().getHeader("CamelHttpResponseText"));
    }

    @Test
    void shouldHandleAllKnownHttpStatusCodes() throws Exception {
        int[] statusCodes = {200, 400, 401, 403, 404, 500, 502, 503};
        String[] expectedTexts = {"OK", "Bad Request", "Unauthorized", "Forbidden", 
                                "Not Found", "Internal Server Error", "Bad Gateway", "Service Unavailable"};
        
        for (int i = 0; i < statusCodes.length; i++) {
            Exchange testExchange = new DefaultExchange(new DefaultCamelContext());
            testExchange.setProperty("backendErrorCode", String.valueOf(statusCodes[i]));
            
            processor.process(testExchange);
            
            assertEquals(expectedTexts[i], testExchange.getIn().getHeader("CamelHttpResponseText"));
        }
    }

    @Test
    void shouldReturnUnknownForUnsupportedStatusCode() throws Exception {
        exchange.setProperty("backendErrorCode", "999");
        
        processor.process(exchange);
        
        assertEquals("Unknown", exchange.getIn().getHeader("CamelHttpResponseText"));
    }

    @Test
    void shouldPreserveSessionIdHeader() throws Exception {
        exchange.getIn().setHeader("sessionId", "test-session-123");
        
        processor.process(exchange);
        
        assertEquals("test-session-123", exchange.getIn().getHeader("sessionId"));
    }

    @Test
    void shouldHandleNullSessionId() throws Exception {
        exchange.getIn().setHeader("sessionId", null);
        
        processor.process(exchange);
        
        assertNull(exchange.getIn().getHeader("sessionId"));
    }

    @Test
    void shouldGenerateValidErrorResponseStructure() throws Exception {
        exchange.setProperty("backendErrorCode", "400");
        exchange.setProperty("backendErrorMessage", "Validation failed");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertTrue(response.has("identificacion"));
        assertTrue(response.has("nombres"));
        assertTrue(response.has("primerApellido"));
        assertTrue(response.has("segundoApellido"));
        assertTrue(response.has("fechaNacimiento"));
        assertTrue(response.has("mensaje"));
        assertTrue(response.has("codigo"));
        
        assertEquals("", response.get("identificacion").get("numeroIdentificacion").asText());
        assertEquals("", response.get("identificacion").get("tipoIdentificacion").asText());
        assertEquals("", response.get("nombres").asText());
        assertEquals("0001-01-01", response.get("fechaNacimiento").asText());
    }

    @Test
    void shouldHandleEmptyErrorMessage() throws Exception {
        exchange.setProperty("backendErrorMessage", "");
        exchange.setProperty("Mensaje", "");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals("Error interno del servidor", response.get("mensaje").asText());
    }

    @Test
    void shouldHandleWhitespaceOnlyErrorMessage() throws Exception {
        exchange.setProperty("backendErrorMessage", "   ");
        exchange.setProperty("Mensaje", "   ");
        
        processor.process(exchange);
        
        JsonNode response = mapper.readTree(exchange.getIn().getBody(String.class));
        
        assertEquals("Error interno del servidor", response.get("mensaje").asText());
    }

    @Test
    void shouldSetBothInAndOutMessageHeaders() throws Exception {
        exchange.setProperty("backendErrorCode", "404");
        
        processor.process(exchange);
        
        assertEquals(404, exchange.getIn().getHeader("CamelHttpResponseCode"));
        assertEquals(404, exchange.getMessage().getHeader("CamelHttpResponseCode"));
        assertEquals("Not Found", exchange.getIn().getHeader("CamelHttpResponseText"));
        assertEquals("Not Found", exchange.getMessage().getHeader("CamelHttpResponseText"));
    }
}