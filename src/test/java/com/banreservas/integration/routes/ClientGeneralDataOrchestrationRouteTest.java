package com.banreservas.integration.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.mocks.ClientDataRestMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(ClientDataRestMock.class)
class ClientGeneralDataOrchestrationRouteTest extends CamelQuarkusTestSupport {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente";
    private static final String SESSION_ID = "123456";

    // =============== TESTS DE VALIDACIÓN (NO REQUIEREN BACKEND) ===============
    
    @Test
    void shouldRejectEmptyRequestBody() {
        System.out.println("Active profile: " + System.getProperty("quarkus.profile"));

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
    void shouldRejectInvalidRNCLength() {
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

    // =============== TESTS EXITOSOS (REQUIEREN BACKEND MOCK) ===============

    @Test
    void shouldProcessRNCRequestSuccessfully() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "101199662",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("identificacion.numeroIdentificacion", is("101199662"))
                .body("identificacion.tipoIdentificacion", is("RNC"))
                .body("nombres", is("BON AGROINDUSTRIAL"));
    }

    @Test
    void shouldProcessCedulaFromMasterDataSuccessfully() {
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
                .statusCode(200)
                .body("identificacion.numeroIdentificacion", is("40233832993"))
                .body("identificacion.tipoIdentificacion", is("Cedula"))
                .body("nombres", is("DOMINGO JUNIOR"));
    }

    @Test
    void shouldProcessCedulaWithForceUpdateFromJCE() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "TRUE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("identificacion.numeroIdentificacion", is("40233832993"))
                .body("identificacion.tipoIdentificacion", is("Cedula"))
                .body("nombres", is("DOMINGO JUNIOR"));
    }

    @Test
    void shouldActivateJCEFlowWhenClientNotFoundInMaster() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "99999999999",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("identificacion.numeroIdentificacion", is("99999999999"))
                .body("identificacion.tipoIdentificacion", is("Cedula"))
                .body("nombres", is("JUAN CARLOS"));
    }

    @Test
    void shouldProcessWithBinaryPhotoTrue() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "40233832993",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "TRUE",
                        "incluirFotoBinaria": "TRUE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .body("identificacion.numeroIdentificacion", is("40233832993"))
                .body("fotoBinario", notNullValue());
    }

    // =============== TESTS DE ERROR (REQUIEREN BACKEND MOCK) ===============

    @Test
    void shouldHandleLegalClientServiceError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "999999999",
                        "tipoIdentificacion": "RNC",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(404)
                .body("mensaje", is("Cliente jurídico no encontrado"));
    }

    @Test
    void shouldHandleMasterDataServiceError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "88888888888",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "FALSE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(500)
                .body("mensaje", is("Error en el servicio de datos maestros"));
    }

    @Test
    void shouldHandleJCEServiceError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", SESSION_ID)
                .body("""
                    {
                        "identificacion": "77777777777",
                        "tipoIdentificacion": "Cedula",
                        "forzarActualizar": "TRUE",
                        "incluirFotoBinaria": "FALSE"
                    }
                    """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(503)
                .body("mensaje", is("Servicio JCE no disponible"));
    }
}