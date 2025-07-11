package com.banreservas.integration.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;

import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.mocks.SoapWireMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * Pruebas de integración para el servicio SOAP ClienteBanreservas.
 * Valida casos exitosos, errores de validación, autenticación y errores del backend.
 * 
 * @author Domingo Junior Ruiz - c-djruiz@banreservas.com
 * @since 20/06/2025
 * @version 1.0.0
 */
@QuarkusTest
@QuarkusTestResource(SoapWireMock.class)
class BanreservasClientRouteTest extends CamelQuarkusTestSupport {

    private static final String AUTH_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI...";
    private static final String URL_ENDPOINT = "/banreservas/client/api/v1/banreservasClient";

    private static String requestOk;
    private static String requestError;
    private static String responseClienteOk;
    private static String responseClienteError;

    @BeforeAll
    static void setup() {
        requestOk = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal>Web</Canal>
                    <Usuario>UsuarioPrueba</Usuario>
                    <Terminal>TerminalPrueba</Terminal>
                    <FechaHora>2025-06-19T14:30:00</FechaHora>
                    <Version>1.0</Version>
                    <Identificacion>
                      <NumeroIdentificacion>40225897896</NumeroIdentificacion>
                      <TipoIdentificacion>Cedula</TipoIdentificacion>
                    </Identificacion>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        requestError = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal></Canal>
                    <Usuario></Usuario>
                    <Terminal></Terminal>
                    <FechaHora></FechaHora>
                    <Version></Version>
                    <Identificacion>
                      <NumeroIdentificacion></NumeroIdentificacion>
                      <TipoIdentificacion></TipoIdentificacion>
                    </Identificacion>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        responseClienteOk = """
            {
              "header": {
                "responseCode": 200,
                "responseMessage": "Success"
              },
              "body": {
                "client": {
                  "status": "Activo",
                  "firstName": "Juan",
                  "middleName": "Carlos",
                  "lastName": "Perez",
                  "maidenName": "Gonzalez",
                  "fullName": "Juan Carlos Perez Gonzalez",
                  "surnames": "Perez Gonzalez",
                  "nickname": "Juanito",
                  "personType": "F",
                  "maritalStatus": "Soltero",
                  "countryOfResidence": "DO",
                  "gender": "M",
                  "birthDate": "1990-01-15",
                  "birthPlace": "Santo Domingo",
                  "birthCountry": "DO",
                  "birthCity": "Santo Domingo",
                  "originCountryCode": "DO",
                  "originCountry": "DO",
                  "isForeigner": false,
                  "residenceType": "Residente",
                  "geographicLevel": "Nacional",
                  "segment": "Premium",
                  "issuingAttentionPoint": "Sucursal001",
                  "supervisor": "Supervisor001",
                  "accountExecutive": "Ejecutivo001",
                  "isOnBlacklist": false,
                  "legalRepresentative": null,
                  "costCenter": "CostCenter001",
                  "clientType": "VIP",
                  "isTopClient": true,
                  "website": null,
                  "isDeceased": false,
                  "deathDate": null,
                  "dependents": 0,
                  "isLinkedToGroupEmployee": false,
                  "isLinkedToBanreservas": true,
                  "controlLists": null,
                  "employment": null,
                  "addresses": [],
                  "contactMethods": [],
                  "emails": [],
                  "relatedSystems": [],
                  "applicationHistory": []
                }
              }
            }
            """;
        
        responseClienteError = """
            {
              "header": {
                "responseCode": 404,
                "responseMessage": "Cliente no encontrado"
              },
              "body": null
            }
            """;

        SoapWireMock.setResponseClienteOk(responseClienteOk);
        SoapWireMock.setResponseClienteError(responseClienteError);
    }

    @Test
    void testClienteBanreservasResponseOk() {
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestOk)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(200)
                .body(containsString("<Resultado>0</Resultado>"))
                .body(containsString("<Mensaje>Consulta exitosa</Mensaje>"));
    }

    @Test
    void testClienteBanreservasValidationError() {
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestError)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400) 
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("obligatorio")));
    }

    @Test
    void testClienteBanreservasUnauthorized() {
        given()
                .contentType(ContentType.XML)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestOk)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400)
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("Unauthorized")));
    }

    @Test
    void testClienteBanreservasWithoutSessionId() {
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestOk)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400)
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("sessionId")));
    }

    @Test
    void testClienteBanreservasBackendError401() {
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "unauthorized") 
                .body(requestOk)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(401) 
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("Token expirado o invalido")));
    }

    @Test
    void testClienteBanreservasBackendError404() {
        String requestWithInvalidData = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal>Web</Canal>
                    <Usuario>UsuarioPrueba</Usuario>
                    <Terminal></Terminal>
                    <FechaHora>2025-06-19T14:30:00</FechaHora>
                    <Version>1.0</Version>
                    <Identificacion>
                      <NumeroIdentificacion>40225897896</NumeroIdentificacion>
                      <TipoIdentificacion>Cedula</TipoIdentificacion>
                    </Identificacion>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestWithInvalidData)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400)
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("Terminal")));
    }

    @Test
    void testBanreservasClientInvalidRequest400() {
        String requestMalformed = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal>Web</Canal>
                    <Usuario>UsuarioPrueba</Usuario>
                    <Terminal>TerminalPrueba</Terminal>
                    <FechaHora>INVALID-DATE</FechaHora>
                    <Version>1.0</Version>
                    <Identificacion>
                      <NumeroIdentificacion>40225897896</NumeroIdentificacion>
                      <TipoIdentificacion>TipoInvalido</TipoIdentificacion>
                    </Identificacion>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestMalformed)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400)
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")));
    }

    @Test
    void testClienteBanreservasBackendError503() {
        String requestWithNullValues = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal>Web</Canal>
                    <Usuario></Usuario>
                    <Terminal>TerminalPrueba</Terminal>
                    <FechaHora>2025-06-19T14:30:00</FechaHora>
                    <Version>1.0</Version>
                    <Identificacion>
                      <NumeroIdentificacion>40225897896</NumeroIdentificacion>
                      <TipoIdentificacion>Cedula</TipoIdentificacion>
                    </Identificacion>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestWithNullValues)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400) 
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("obligatorio")));
    }

    @Test
    void testClienteBanreservasInvalidSOAPEnvelope() {
        String invalidXml = "<invalid>xml</invalid>";
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(invalidXml)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(500); 
    }

    @Test
    void testClienteBanreservasEmptyRequest() {
        String emptyXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
              </soap:Body>
            </soap:Envelope>
            """;
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(emptyXml)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(500) 
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")));
    }

    @Test
    void testClienteBanreservasMissingIdentification() {
        String requestWithoutId = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <ClienteBanreservas xmlns="http://tempuri.org/">
                  <request>
                    <Canal>Web</Canal>
                    <Usuario>UsuarioPrueba</Usuario>
                    <Terminal>TerminalPrueba</Terminal>
                    <FechaHora>2025-06-19T14:30:00</FechaHora>
                    <Version>1.0</Version>
                  </request>
                </ClienteBanreservas>
              </soap:Body>
            </soap:Envelope>
            """;
        
        given()
                .contentType(ContentType.XML)
                .header("Authorization", AUTH_TOKEN)
                .header("sessionId", "123456")
                .header("SOAPAction", "http://tempuri.org/ClienteBanreservas")
                .header("test", "success")
                .body(requestWithoutId)
                .post(URL_ENDPOINT)
                .then()
                .statusCode(400) 
                .body(hasXPath("//*[local-name()='resultado']", equalTo("1")))
                .body(hasXPath("//*[local-name()='mensaje']", containsString("Identificacion")));
    }
}