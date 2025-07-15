package com.banreservas.integration.routes;

import com.banreservas.integration.mocks.RestMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(RestMock.class)
class ClientGeneralDataOrchestrationRouteTest {

    private static final String ENDPOINT = "/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente";

    // ========== TESTS EXITOSOS ==========
    
    @Test
    void shouldCallLegalClientRouteWithRNC() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "123")
            .body("""
                {
                    "identificacion": "101199662",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200)
            .body("nombres", equalTo("BON AGROINDUSTRIAL"));
    }

    @Test
    void shouldCallMasterDataWithCedula() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "123")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldCallJceAndUpdateWithCedula() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "123")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200)
            .body("primerApellido", equalTo("RAMIREZ"));
    }

    @Test
    void shouldActivateDynamicJceFlowOn904() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "dynamic-904")
            .body("""
                {
                    "identificacion": "90490490490",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200)
            .body("primerApellido", equalTo("DINAMICO"));
    }

    // ========== TESTS DE ERROR 401 (UNAUTHORIZED) ==========
    
    @Test
    void shouldHandleUnauthorizedErrorFromLegalClient() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "unauthorized-legal")
            .body("""
                {
                    "identificacion": "401401401",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(401);
    }

    @Test
    void shouldHandleUnauthorizedErrorFromMasterData() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "unauthorized-master")
            .body("""
                {
                    "identificacion": "40140140140",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(401);
    }

    @Test
    void shouldHandleUnauthorizedErrorFromJCE() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "unauthorized-jce")
            .body("""
                {
                    "identificacion": "40140140140",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(401);
    }

    @Test
    void shouldHandleUnauthorizedErrorFromUpdateMaster() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "unauthorized-update")
            .body("""
                {
                    "identificacion": "40140140140",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "true"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(401);
    }

    // ========== TESTS DE ERROR 404 (NOT FOUND) ==========
    
    @Test
    void shouldHandleNotFoundErrorFromLegalClient() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "notfound-legal")
            .body("""
                {
                    "identificacion": "404404404",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldHandleNotFoundErrorFromMasterData() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "notfound-master")
            .body("""
                {
                    "identificacion": "40440440440",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldHandleNotFoundErrorFromJCE() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "notfound-jce")
            .body("""
                {
                    "identificacion": "40440440440",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(404);
    }

    @Test
    void shouldHandleNotFoundErrorFromUpdateMaster() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "notfound-update")
            .body("""
                {
                    "identificacion": "40440440440",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "true"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(404);
    }

    // ========== TESTS DE ERROR 500 (INTERNAL SERVER ERROR) ==========
    
    @Test
    void shouldHandleInternalServerErrorFromLegalClient() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "error-legal")
            .body("""
                {
                    "identificacion": "500500500",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(500);
    }

    @Test
    void shouldHandleInternalServerErrorFromMasterData() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "error-master")
            .body("""
                {
                    "identificacion": "50050050050",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(500);
    }

    @Test
    void shouldHandleInternalServerErrorFromJCE() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "error-jce")
            .body("""
                {
                    "identificacion": "50050050050",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(500);
    }

    @Test
    void shouldHandleInternalServerErrorFromUpdateMaster() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "error-update")
            .body("""
                {
                    "identificacion": "50050050050",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "true"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(500);
    }

    // ========== TESTS DE VALIDACIÓN ==========
    
    @Test
    void shouldReturn400WhenIdentificationIsMissing() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "missing-id")
            .body("""
                {
                    "identificacion": "",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenIdentificationTypeIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "invalid-type")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Pasaporte",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenCedulaFormatIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "invalid-cedula")
            .body("""
                {
                    "identificacion": "ABC12345678",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenRncFormatIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "invalid-rnc")
            .body("""
                {
                    "identificacion": "ABC123456",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenBooleanValueIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "invalid-boolean")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "MAYBE",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenJsonIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "invalid-json")
            .body("{identificacion: invalid}")
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturn400WhenRequestBodyIsEmpty() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "empty-body")
            .body("")
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    // ========== TESTS PARA CASE INSENSITIVE Y OTROS ==========
    
    @Test
    void shouldHandleCaseInsensitiveValues() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "case-insensitive")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "cedula",
                    "forzarActualizar": "TRUE",
                    "incluirFotoBinaria": "False"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldHandleDefaultValues() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "defaults")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldPreserveSessionId() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "preserve-session-123")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200)
            .header("sessionId", equalTo("preserve-session-123"));
    }

    @Test
    void shouldHandleAllStandardHeaders() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "all-headers")
            .header("Canal", "MICM")
            .header("Usuario", "testuser")
            .header("Terminal", "terminal01")
            .header("FechaHora", "2025-01-15T10:30:00")
            .header("Version", "1.0")
            .header("Servicio", "ConsultarDatosGeneralesCliente")
            .header("Authorization", "Bearer token123")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    // ========== TESTS ADICIONALES PARA COBERTURA ==========
    
    @Test
    void shouldAcceptRncWith9Digits() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "rnc-9digits")
            .body("""
                {
                    "identificacion": "123456789",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldAcceptRncWith11Digits() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "rnc-11digits")
            .body("""
                {
                    "identificacion": "12345678901",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldRejectRncTooShort() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "rnc-short")
            .body("""
                {
                    "identificacion": "12345",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldRejectRncTooLong() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "rnc-long")
            .body("""
                {
                    "identificacion": "123456789012",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldRejectCedulaTooShort() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "cedula-short")
            .body("""
                {
                    "identificacion": "1234567890",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldRejectCedulaTooLong() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "cedula-long")
            .body("""
                {
                    "identificacion": "123456789012",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldAcceptCedulaWithLeadingZeros() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "cedula-zeros")
            .body("""
                {
                    "identificacion": "00225005308",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldTestBooleanNormalization() {
        // TRUE/FALSE en mayúsculas
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "bool-upper")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "TRUE",
                    "incluirFotoBinaria": "FALSE"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);

        // true/false en minúsculas
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "bool-lower")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);

        // Mixed case
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "bool-mixed")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "True",
                    "incluirFotoBinaria": "False"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldExecuteAllOrchestrationBranches() {
        // Rama RNC
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "branch-rnc")
            .body("""
                {
                    "identificacion": "101199662",
                    "tipoIdentificacion": "RNC",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);

        // Rama Cedula sin forzar (Master Data)
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "branch-master")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "false",
                    "incluirFotoBinaria": "false"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);

        // Rama Cedula forzando (JCE)
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "branch-jce")
            .body("""
                {
                    "identificacion": "22500530872",
                    "tipoIdentificacion": "Cedula",
                    "forzarActualizar": "true",
                    "incluirFotoBinaria": "true"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(200);
    }

    @Test
    void shouldTestAllValidationBranches() {
        // Identificación nula
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "validation-null-id")
            .body("""
                {
                    "tipoIdentificacion": "Cedula"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);

        // Tipo de identificación nulo
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "validation-null-type")
            .body("""
                {
                    "identificacion": "22500530872"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);

        // Identificación con espacios
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "validation-space-id")
            .body("""
                {
                    "identificacion": "   ",
                    "tipoIdentificacion": "Cedula"
                }
            """)
            .when()
            .post(ENDPOINT)
            .then()
            .statusCode(400);
    }
}