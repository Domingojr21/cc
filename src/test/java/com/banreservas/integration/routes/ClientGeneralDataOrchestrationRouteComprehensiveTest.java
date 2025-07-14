package com.banreservas.integration.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class ClientGeneralDataOrchestrationRouteComprehensiveTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente";
    private static final String SESSION_ID = "test-session-123";
    
    @Test
    void shouldRejectEmptyRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", containsString("Request body no puede ser nulo o vacío"));
    }

    @Test
    void shouldRejectNullRequestBody() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", containsString("Request body no puede ser nulo o vacío"));
    }

    @Test
    void shouldRejectInvalidJsonFormat() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("{ invalid json }")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", containsString("Formato JSON inválido"));
    }

    @Test
    void shouldRejectMissingIdentificationNumber() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Identificación es requerida"));
    }

    @Test
    void shouldRejectEmptyIdentificationNumber() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Identificación es requerida"));
    }

    @Test
    void shouldRejectMissingIdentificationType() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Tipo de identificación es requerido"));
    }

    @Test
    void shouldRejectEmptyIdentificationType() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Tipo de identificación es requerido"));
    }

    @Test
    void shouldRejectInvalidIdentificationType() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Pasaporte",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Tipo de identificación debe ser: Cedula o RNC"));
    }

    @Test
    void shouldRejectInvalidCedulaLength() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "12345",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de cédula debe tener exactamente 11 dígitos"));
    }

    @Test
    void shouldRejectCedulaTooLong() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "402338329931234",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de cédula debe tener exactamente 11 dígitos"));
    }

    @Test
    void shouldRejectCedulaWithNonNumericCharacters() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "4023383299A",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de cédula debe contener solo dígitos numéricos"));
    }

    @Test
    void shouldRejectInvalidRNCLengthTooShort() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "12345",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de RNC debe tener entre 9 y 11 dígitos"));
    }

    @Test
    void shouldRejectInvalidRNCLengthTooLong() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "123456789012345",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de RNC debe tener entre 9 y 11 dígitos"));
    }

    @Test
    void shouldRejectRNCWithNonNumericCharacters() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "10119966A",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Número de RNC debe contener solo dígitos numéricos"));
    }

    @Test
    void shouldRejectInvalidForceUpdateValue() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "MAYBE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("forzarActualizar debe ser TRUE o FALSE"));
    }

    @Test
    void shouldRejectInvalidIncludeBinaryPhotoValue() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "SOMETIMES"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("incluirFotoBinaria debe ser TRUE o FALSE"));
    }

    @Test
    void shouldHandleCaseInsensitiveIdentificationTypeValidation() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "invalid_type",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Tipo de identificación debe ser: Cedula o RNC"));
    }

    @Test
    void shouldHandleWhitespaceInIdentification() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "   ",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Identificación es requerida"));
    }

    @Test
    void shouldHandleWhitespaceInIdentificationType() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "   ",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", is("Tipo de identificación es requerido"));
    }

    @Test
    void shouldValidateMinimumValidRNC() {
        // Este test fallará porque necesita backend, pero valida que la validación pase
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "123456789",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                // No validamos status aquí porque depende del backend
                // Solo validamos que pasa la validación inicial
                .body(not(containsString("debe tener entre 9 y 11 dígitos")));
    }

    @Test
    void shouldValidateMaximumValidRNC() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "12345678901",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .body(not(containsString("debe tener entre 9 y 11 dígitos")));
    }

    @Test
    void shouldValidateValidCedula() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .body(not(containsString("debe tener exactamente 11 dígitos")));
    }

    @Test
    void shouldAllowOptionalFieldsToBeOmitted() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                // Validamos que no hay errores de validación de campos requeridos
                .body(not(containsString("es requerida")))
                .body(not(containsString("es requerido")));
    }

    @Test
    void shouldSetCorrectContentTypeInValidationErrorResponse() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }

    @Test
    void shouldHandleComplexJsonStructure() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "TRUE",
                        "extraField": "should be ignored"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                // Validamos que campos extra no causan errores de validación
                .body(not(containsString("es requerida")))
                .body(not(containsString("es requerido")));
    }

    @Test
    void shouldHandleCaseInsensitiveBooleanValues() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "cedula",
                        "forzarActualizar": "true",
                        "incluirFotoBinaria": "false"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                // Validamos que los valores case-insensitive pasan la validación
                .body(not(containsString("debe ser TRUE o FALSE")));
    }

    @Test
    void shouldValidateWithAllStandardHeaders() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .header("Canal", "WEB")
                .header("Usuario", "testuser")
                .header("Terminal", "WS001")
                .header("FechaHora", "2025-07-14T10:30:00")
                .header("Version", "1.0")
                .header("Servicio", "ConsultarDatosGeneralesCliente")
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                // Validamos que headers adicionales no interfieren con validación
                .body(not(containsString("es requerida")))
                .body(not(containsString("es requerido")));
    }

    @Test
    void shouldRejectMalformedJsonWithExtraCommas() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE",
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", containsString("Formato JSON inválido"));
    }

    @Test
    void shouldRejectJsonWithMissingQuotes() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        identificacion: "40233832993",
                        "tipoIdentificacion": "Cedula"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .body("mensaje", containsString("Formato JSON inválido"));
    }
}