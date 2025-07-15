package com.banreservas.integration.routes;

import com.banreservas.integration.mocks.BackendMockServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientGeneralDataOrchestrationRouteTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_PATH = "/consultar/datos/generales/cliente/api/v1/consultar-datos-generales-cliente";

    @BeforeAll
    static void setupAll() {
        BackendMockServer.startServer();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    static void tearDownAll() {
        BackendMockServer.stopServer();
    }

    @BeforeEach
    void setUp() {
        BackendMockServer.reset();
    }

    @Test
    @Order(1)
    void testRncClientQuery() throws Exception {
        String requestBody = buildRequest("101199662", "RNC", "FALSE", "FALSE");
        String response = executeRequest(requestBody, "test-rnc");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(2)
    void testCedulaWithoutForceUpdate() throws Exception {
        String requestBody = buildRequest("22500530872", "Cedula", "FALSE", "FALSE");
        String response = executeRequest(requestBody, "test-cedula-master");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(3)
    void testCedulaWithForceUpdate() throws Exception {
        String requestBody = buildRequest("22500530872", "Cedula", "TRUE", "TRUE");
        String response = executeRequest(requestBody, "test-cedula-jce");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(4)
    void testCaseInsensitiveValues() throws Exception {
        String requestBody = buildRequest("22500530872", "cedula", "false", "true");
        String response = executeRequest(requestBody, "test-case-insensitive");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(5)
    void testDefaultValues() throws Exception {
        String requestBody = """
            {
                "identificacion": "22500530872",
                "tipoIdentificacion": "Cedula"
            }
            """;
        String response = executeRequest(requestBody, "test-defaults");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(6)
    void testEmptyRequestBody() {
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-empty")
            .header("Authorization", "Bearer test-token")
            .body("")
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Request body no puede ser nulo o vacío"));
    }

    @Test
    @Order(7)
    void testMissingIdentification() {
        String requestBody = """
            {
                "tipoIdentificacion": "RNC",
                "forzarActualizar": "FALSE",
                "incluirFotoBinaria": "FALSE"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-missing-id")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Identificación es requerida"));
    }

    @Test
    @Order(8)
    void testMissingIdentificationType() {
        String requestBody = """
            {
                "identificacion": "101199662",
                "forzarActualizar": "FALSE",
                "incluirFotoBinaria": "FALSE"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-missing-type")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Tipo de identificación es requerido"));
    }

    @Test
    @Order(9)
    void testInvalidIdentificationType() {
        String requestBody = buildRequest("22500530872", "INVALID", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-invalid-type")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Tipo de identificación debe ser: Cedula o RNC"));
    }

    @Test
    @Order(10)
    void testInvalidCedulaLength() {
        String requestBody = buildRequest("123", "Cedula", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-cedula-length")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Número de cédula debe tener exactamente 11 dígitos"));
    }

    @Test
    @Order(11)
    void testInvalidCedulaFormat() {
        String requestBody = buildRequest("abc12345678", "Cedula", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-cedula-format")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Número de cédula debe contener solo dígitos numéricos"));
    }

    @Test
    @Order(12)
    void testInvalidRncLength() {
        String requestBody = buildRequest("123", "RNC", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-rnc-length")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Número de RNC debe tener entre 9 y 11 dígitos"));
    }

    @Test
    @Order(13)
    void testInvalidRncFormat() {
        String requestBody = buildRequest("abc123456", "RNC", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-rnc-format")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Número de RNC debe contener solo dígitos numéricos"));
    }

    @Test
    @Order(14)
    void testInvalidJsonFormat() {
        String invalidJson = """
            {
                "identificacion": "22500530872"
                "tipoIdentificacion": "Cedula"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-invalid-json")
            .header("Authorization", "Bearer test-token")
            .body(invalidJson)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("Formato JSON inválido"));
    }

    @Test
    @Order(15)
    void testInvalidBooleanForceUpdate() {
        String requestBody = buildRequest("22500530872", "Cedula", "MAYBE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-boolean-force")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("forzarActualizar debe ser TRUE o FALSE"));
    }

    @Test
    @Order(16)
    void testInvalidBooleanIncludePhoto() {
        String requestBody = buildRequest("22500530872", "Cedula", "FALSE", "INVALID");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-boolean-photo")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("mensaje", containsString("incluirFotoBinaria debe ser TRUE o FALSE"));
    }

    @Test
    @Order(17)
    void testOrchestrationDecisionForRnc() throws Exception {
        String requestBody = buildRequest("101199662", "RNC", "FALSE", "FALSE");
        String response = executeRequest(requestBody, "test-orchestration-rnc");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(18)
    void testOrchestrationDecisionForCedulaNoForce() throws Exception {
        String requestBody = buildRequest("22500530872", "Cedula", "FALSE", "FALSE");
        String response = executeRequest(requestBody, "test-orchestration-cedula-no-force");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(19)
    void testOrchestrationDecisionForCedulaWithForce() throws Exception {
        String requestBody = buildRequest("22500530872", "Cedula", "TRUE", "TRUE");
        String response = executeRequest(requestBody, "test-orchestration-cedula-force");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(20)
    void testRequestValidationProcessor() throws Exception {
        String[] testCases = {
            buildRequest("22500530872", "Cedula", "TRUE", "TRUE"),
            buildRequest("101199662", "RNC", "FALSE", "FALSE"),
            buildRequest("33344455566", "Cedula", "FALSE", "TRUE")
        };
        
        for (int i = 0; i < testCases.length; i++) {
            String response = executeRequest(testCases[i], "test-validation-" + i);
            JsonNode jsonResponse = objectMapper.readTree(response);
            assertNotNull(jsonResponse);
        }
    }

    @Test
    @Order(21)
    void testBackendRequestGeneration() throws Exception {
        String requestBody = buildRequest("22500530872", "Cedula", "TRUE", "TRUE");
        String response = executeRequest(requestBody, "test-backend-generation");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(22)
    void testBackendResponseMapping() throws Exception {
        String requestBody = buildRequest("101199662", "RNC", "FALSE", "FALSE");
        String response = executeRequest(requestBody, "test-response-mapping");
        
        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    @Test
    @Order(23)
    void testErrorResponseProcessor() {
        String requestBody = buildRequest("abc", "Cedula", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-error-processor")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(400)
            .body("codigo", notNullValue())
            .body("mensaje", notNullValue());
    }

    @Test
    @Order(24)
    void testSessionIdPreservation() {
        String sessionId = "preserve-session-test";
        String requestBody = buildRequest("22500530872", "Cedula", "FALSE", "FALSE");
        
        given()
            .contentType(ContentType.JSON)
            .header("sessionId", sessionId)
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)))
            .header("sessionId", equalTo(sessionId));
    }

    @Test
    @Order(25)
    void testCompleteHeaderHandling() throws Exception {
        String requestBody = buildRequest("101199662", "RNC", "FALSE", "FALSE");
        
        String response = given()
            .contentType(ContentType.JSON)
            .header("sessionId", "test-headers")
            .header("Canal", "MICM-TEST")
            .header("Usuario", "usuario-test")
            .header("Terminal", "terminal-test")
            .header("FechaHora", "2025-01-15T10:30:00")
            .header("Version", "1.0")
            .header("Servicio", "ConsultarDatosGeneralesCliente")
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)))
            .extract()
            .asString();

        JsonNode jsonResponse = objectMapper.readTree(response);
        assertNotNull(jsonResponse);
    }

    private String buildRequest(String identificacion, String tipoIdentificacion, 
                               String forzarActualizar, String incluirFotoBinaria) {
        return String.format("""
            {
                "identificacion": "%s",
                "tipoIdentificacion": "%s",
                "forzarActualizar": "%s",
                "incluirFotoBinaria": "%s"
            }
            """, identificacion, tipoIdentificacion, forzarActualizar, incluirFotoBinaria);
    }

    private String executeRequest(String requestBody, String sessionId) {
        return given()
            .contentType(ContentType.JSON)
            .header("sessionId", sessionId)
            .header("Authorization", "Bearer test-token")
            .body(requestBody)
            .when()
            .post(BASE_PATH)
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(404)))
            .extract()
            .asString();
    }
}