package com.banreservas.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ClientDataRestMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();

        // Stubs específicos primero (máxima prioridad)
        stubSpecificClientJuridicoResponses();
        stubSpecificMaestroResponses();
        stubSpecificJCEResponses();
        
        // Stubs generales
        stubSuccessfulClientJuridico();
        stubFailedClientJuridico();
        stubSuccessfulMaestro();
        stubFailedMaestro();
        stubSuccessfulJCE();
        stubFailedJCE();
        stubSuccessfulUpdateMaestro();
        
        // Catch-all al final (mínima prioridad)
        stubCatchAllStubs();

        return Map.of(
            "consultar.datos.generales.cliente.juridico.url", "http://localhost:8090/ms-consultar-datos-generales-cliente-juridico",
            "consultar.datos.maestro.cedulados.url", "http://localhost:8090/consultar-datos-maestro-cedulados",
            "consultar.datos.jcedp.url", "http://localhost:8090/consulta-jce",
            "actualizar.datos.maestro.cedulados.url", "http://localhost:8090/ms-actualizar-datos-maestro-cedulados"
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            System.out.println("WireMock server stopped");
        }
    }

    private void stubSpecificClientJuridicoResponses() {
        // RNC exitoso - 101199662
        wireMockServer.stubFor(post(urlEqualTo("/ms-consultar-datos-generales-cliente-juridico"))
                .withRequestBody(containing("101199662"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Exitoso"
                                },
                                "body": {
                                    "client": {
                                        "identification": {
                                            "number": "101199662",
                                            "type": "RNC"
                                        },
                                        "businessName": "BON AGROINDUSTRIAL"
                                    }
                                }
                            }
                            """)));
    }

    private void stubSpecificMaestroResponses() {
        // Cédula exitosa - 40233832993
        wireMockServer.stubFor(post(urlEqualTo("/consultar-datos-maestro-cedulados"))
                .withRequestBody(containing("40233832993"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Exitoso"
                                },
                                "body": {
                                    "code": "000",
                                    "clients": [
                                        {
                                            "identifications": [
                                                {
                                                    "number": "40233832993",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "names": "DOMINGO JUNIOR",
                                            "firstName": "DOMINGO",
                                            "middleLastName": "RUIZ MELENCIANO",
                                            "dateOfBirth": "2003-02-21T00:00:00",
                                            "placeOfBirth": "SANTO DOMINGO, R.D.",
                                            "sex": "M",
                                            "maritalStatus": "S",
                                            "category": "Mayor de Edad",
                                            "cancellationCause": "",
                                            "stateID": "1",
                                            "cancellationDate": "2024-02-21T00:00:00",
                                            "photo": ""
                                        }
                                    ]
                                }
                            }
                            """)));

        // Error maestro - 88888888888
        wireMockServer.stubFor(post(urlEqualTo("/consultar-datos-maestro-cedulados"))
                .withRequestBody(containing("88888888888"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 500,
                                    "responseMessage": "Error en el servicio de datos maestros"
                                }
                            }
                            """)));
    }

    private void stubSpecificJCEResponses() {
        // JCE con foto
        wireMockServer.stubFor(post(urlEqualTo("/consulta-jce"))
                .withRequestBody(containing("\"includeBinaryPhoto\":true"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Consulta exitosa"
                                },
                                "body": {
                                    "clients": [
                                        {
                                            "identifications": [
                                                {
                                                    "number": "40233832993",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "names": "DOMINGO JUNIOR",
                                            "firstSurname": "RUIZ",
                                            "secondSurname": "MELENCIANO",
                                            "birthDate": "2003-02-21T05:00:00.000Z",
                                            "birthPlace": "SANTO DOMINGO, R.D.",
                                            "gender": "M",
                                            "maritalStatus": "S",
                                            "categoryId": "0",
                                            "category": "Mayor de Edad",
                                            "photoBinary": "/9j/4AAQSkZJRgABAQEAYABgAAD/"
                                        }
                                    ]
                                }
                            }
                            """)));

        // JCE para flujo dinámico - 99999999999
        wireMockServer.stubFor(post(urlEqualTo("/consulta-jce"))
                .withRequestBody(containing("99999999999"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Consulta exitosa"
                                },
                                "body": {
                                    "clients": [
                                        {
                                            "identifications": [
                                                {
                                                    "number": "99999999999",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "names": "JUAN CARLOS",
                                            "firstSurname": "PEREZ",
                                            "secondSurname": "GONZALEZ",
                                            "birthDate": "1990-05-15T05:00:00.000Z",
                                            "birthPlace": "SANTIAGO, R.D.",
                                            "gender": "M",
                                            "maritalStatus": "C",
                                            "categoryId": "0",
                                            "category": "Mayor de Edad",
                                            "photoBinary": ""
                                        }
                                    ]
                                }
                            }
                            """)));
    }

    private void stubSuccessfulClientJuridico() {
        // RNC exitoso genérico
        wireMockServer.stubFor(post(urlEqualTo("/ms-consultar-datos-generales-cliente-juridico"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Exitoso"
                                },
                                "body": {
                                    "client": {
                                        "identification": {
                                            "number": "123456789",
                                            "type": "RNC"
                                        },
                                        "businessName": "EMPRESA EJEMPLO S.A."
                                    }
                                }
                            }
                            """)));
    }

    private void stubFailedClientJuridico() {
        // RNC error - 999999999
        wireMockServer.stubFor(post(urlEqualTo("/ms-consultar-datos-generales-cliente-juridico"))
                .withRequestBody(containing("999999999"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 404,
                                    "responseMessage": "Cliente jurídico no encontrado"
                                }
                            }
                            """)));
    }

    private void stubSuccessfulMaestro() {
        // Cliente no encontrado - 99999999999 (código 904)
        wireMockServer.stubFor(post(urlEqualTo("/consultar-datos-maestro-cedulados"))
                .withRequestBody(containing("99999999999"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Cliente no encontrado"
                                },
                                "body": {
                                    "code": "904",
                                    "message": "Cliente no encontrado en datos maestros"
                                }
                            }
                            """)));
    }

    private void stubFailedMaestro() {
        // Error genérico maestro
        wireMockServer.stubFor(post(urlEqualTo("/consultar-datos-maestro-cedulados"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 500,
                                    "responseMessage": "Error en el servicio de datos maestros"
                                }
                            }
                            """)));
    }

    private void stubSuccessfulJCE() {
        // JCE sin foto - cualquier cédula
        wireMockServer.stubFor(post(urlEqualTo("/consulta-jce"))
                .withRequestBody(containing("\"includeBinaryPhoto\":false"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "Consulta exitosa"
                                },
                                "body": {
                                    "clients": [
                                        {
                                            "identifications": [
                                                {
                                                    "number": "40233832993",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "names": "DOMINGO JUNIOR",
                                            "firstSurname": "RUIZ",
                                            "secondSurname": "MELENCIANO",
                                            "birthDate": "2003-02-21T05:00:00.000Z",
                                            "birthPlace": "SANTO DOMINGO, R.D.",
                                            "gender": "M",
                                            "maritalStatus": "S",
                                            "categoryId": "0",
                                            "category": "Mayor de Edad",
                                            "photoBinary": ""
                                        }
                                    ]
                                }
                            }
                            """)));
    }

    private void stubFailedJCE() {
        // JCE error - 77777777777
        wireMockServer.stubFor(post(urlEqualTo("/consulta-jce"))
                .withRequestBody(containing("77777777777"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 503,
                                    "responseMessage": "Servicio JCE no disponible"
                                }
                            }
                            """)));
    }

    private void stubSuccessfulUpdateMaestro() {
        // Actualización exitosa
        wireMockServer.stubFor(post(urlEqualTo("/ms-actualizar-datos-maestro-cedulados"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 200,
                                    "responseMessage": "TRANSACCION PROCESADA"
                                },
                                "body": {
                                    "clients": [
                                        {
                                            "identifications": [
                                                {
                                                    "number": "40233832993",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "name": "DOMINGO JUNIOR"
                                        }
                                    ]
                                }
                            }
                            """)));
    }

    private void stubCatchAllStubs() {
        // Catch-all para cliente jurídico
        wireMockServer.stubFor(post(urlEqualTo("/ms-consultar-datos-generales-cliente-juridico"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 400,
                                    "responseMessage": "Solicitud inválida"
                                }
                            }
                            """)));

        // Catch-all para maestro
        wireMockServer.stubFor(post(urlEqualTo("/consultar-datos-maestro-cedulados"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 400,
                                    "responseMessage": "Solicitud inválida"
                                }
                            }
                            """)));

        // Catch-all para JCE
        wireMockServer.stubFor(post(urlEqualTo("/consulta-jce"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 400,
                                    "responseMessage": "Solicitud inválida"
                                }
                            }
                            """)));

        // Catch-all para actualizar
        wireMockServer.stubFor(post(urlEqualTo("/ms-actualizar-datos-maestro-cedulados"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "header": {
                                    "responseCode": 400,
                                    "responseMessage": "Solicitud inválida"
                                }
                            }
                            """)));

        System.out.println("✅ Mock responses configured successfully");
    }
}