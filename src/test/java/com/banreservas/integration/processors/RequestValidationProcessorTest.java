package com.banreservas.integration.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.util.Constants;

class RequestValidationProcessorTest {

    private RequestValidationProcessor processor;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        processor = new RequestValidationProcessor();
        exchange = new DefaultExchange(new DefaultCamelContext());
    }

    @Test
    void shouldValidateValidCedulaRequest() throws Exception {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Cedula",
                "forzarActualizar": "FALSE",
                "incluirFotoBinaria": "FALSE"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        exchange.getIn().setHeader("sessionId", "test123");
        
        processor.process(exchange);
        
        assertEquals("40233832993", exchange.getProperty("identificationNumberRq"));
        assertEquals("Cedula", exchange.getProperty("identificationTypeRq"));
        assertEquals("FALSE", exchange.getProperty("forceUpdateRq"));
        assertEquals("FALSE", exchange.getProperty("includeBinaryPhotoRq"));
    }

    @Test
    void shouldValidateValidRNCRequest() throws Exception {
        String requestBody = """
            {
                "identificacion": "101199662",
                "tipoIdentificacion": "RNC",
                "forzarActualizar": "TRUE",
                "incluirFotoBinaria": "TRUE"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        
        processor.process(exchange);
        
        assertEquals("101199662", exchange.getProperty("identificationNumberRq"));
        assertEquals("RNC", exchange.getProperty("identificationTypeRq"));
        assertEquals("TRUE", exchange.getProperty("forceUpdateRq"));
        assertEquals("TRUE", exchange.getProperty("includeBinaryPhotoRq"));
    }

    @Test
    void shouldNormalizeCaseInsensitiveValues() throws Exception {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "cedula",
                "forzarActualizar": "true",
                "incluirFotoBinaria": "false"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        
        processor.process(exchange);
        
        assertEquals("Cedula", exchange.getProperty("identificationTypeRq"));
        assertEquals("TRUE", exchange.getProperty("forceUpdateRq"));
        assertEquals("FALSE", exchange.getProperty("includeBinaryPhotoRq"));
    }

    @Test
    void shouldUseDefaultValuesWhenOptionalFieldsAreMissing() throws Exception {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Cedula"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        
        processor.process(exchange);
        
        assertEquals("FALSE", exchange.getProperty("forceUpdateRq"));
        assertEquals("FALSE", exchange.getProperty("includeBinaryPhotoRq"));
    }

    @Test
    void shouldThrowValidationExceptionForEmptyBody() {
        exchange.getIn().setBody("");
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Request body no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForNullBody() {
        exchange.getIn().setBody(null);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Request body no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidJson() {
        exchange.getIn().setBody("{ invalid json }");
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertTrue(exception.getMessage().contains("Formato JSON inválido"));
    }

    @Test
    void shouldThrowValidationExceptionForMissingIdentification() {
        String requestBody = """
            {
                "tipoIdentificacion": "Cedula"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Identificación es requerida", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForEmptyIdentification() {
        String requestBody = """
            {
                "identificacion": "",
                "tipoIdentificacion": "Cedula"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Identificación es requerida", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForMissingIdentificationType() {
        String requestBody = """
            {
                "identificacion": "40233832993"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Tipo de identificación es requerido", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidIdentificationType() {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Pasaporte"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Tipo de identificación debe ser: Cedula o RNC", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidCedulaLength() {
        String requestBody = """
            {
                "identificacion": "12345",
                "tipoIdentificacion": "Cedula"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Número de cédula debe tener exactamente 11 dígitos", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForCedulaWithNonNumericCharacters() {
        String requestBody = """
            {
                "identificacion": "4023383299A",
                "tipoIdentificacion": "Cedula"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Número de cédula debe contener solo dígitos numéricos", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidRNCLength() {
        String requestBody = """
            {
                "identificacion": "12345",
                "tipoIdentificacion": "RNC"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Número de RNC debe tener entre 9 y 11 dígitos", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForRNCWithNonNumericCharacters() {
        String requestBody = """
            {
                "identificacion": "10119966A",
                "tipoIdentificacion": "RNC"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("Número de RNC debe contener solo dígitos numéricos", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidForceUpdate() {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Cedula",
                "forzarActualizar": "MAYBE"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("forzarActualizar debe ser TRUE o FALSE", exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionForInvalidIncludeBinaryPhoto() {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Cedula",
                "incluirFotoBinaria": "SOMETIMES"
            }
            """;
        exchange.getIn().setBody(requestBody);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> processor.process(exchange));
        assertEquals("incluirFotoBinaria debe ser TRUE o FALSE", exception.getMessage());
    }

    @Test
    void shouldValidateValidRNCWithMinimumLength() throws Exception {
        String requestBody = """
            {
                "identificacion": "123456789",
                "tipoIdentificacion": "RNC"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        
        processor.process(exchange);
        
        assertEquals("123456789", exchange.getProperty("identificationNumberRq"));
        assertEquals("RNC", exchange.getProperty("identificationTypeRq"));
    }

    @Test
    void shouldValidateValidRNCWithMaximumLength() throws Exception {
        String requestBody = """
            {
                "identificacion": "12345678901",
                "tipoIdentificacion": "RNC"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        
        processor.process(exchange);
        
        assertEquals("12345678901", exchange.getProperty("identificationNumberRq"));
        assertEquals("RNC", exchange.getProperty("identificationTypeRq"));
    }

    @Test
    void shouldPreserveOriginalHeaders() throws Exception {
        String requestBody = """
            {
                "identificacion": "40233832993",
                "tipoIdentificacion": "Cedula"
            }
            """;
        
        exchange.getIn().setBody(requestBody);
        exchange.getIn().setHeader("sessionId", "test123");
        exchange.getIn().setHeader("Canal", "WEB");
        exchange.getIn().setHeader("Usuario", "testuser");
        
        processor.process(exchange);
        
        assertEquals("test123", exchange.getProperty("originalSessionId"));
        assertEquals("WEB", exchange.getProperty("canalRq"));
        assertEquals("testuser", exchange.getProperty("usuarioRq"));
    }
}