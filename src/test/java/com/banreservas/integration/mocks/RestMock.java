package com.banreservas.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RestMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        configureJurClientMock();
        configureMasterDataMock();
        configureJceMock();
        configureUpdateMasterMock();

        return Map.of(
            "consultar.datos.generales.cliente.juridico.url", "http://localhost:8089/api/v1/ms-consultar-datos-generales-cliente-juridico",
            "consultar.datos.maestro.cedulados.url", "http://localhost:8089/api/v1/consultar-datos-maestro-cedulados",
            "consultar.datos.jcedp.url", "http://localhost:8089/api/v1/consulta-jce",
            "actualizar.datos.maestro.cedulados.url", "http://localhost:8089/api/v1/ms-actualizar-datos-maestro-cedulados"
        );
    }

    private void configureJurClientMock() {
        // Mock exitoso para RNC
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-consultar-datos-generales-cliente-juridico"))
            .willReturn(okJson("""
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

        // Error 401 para RNC (usando RNC válido)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-consultar-datos-generales-cliente-juridico"))
            .withRequestBody(containing("\"number\":\"401401401\""))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 401,
                        "responseMessage": "No autorizado"
                      },
                      "body": {
                        "message": "Token invalido o expirado"
                      }
                    }
                """)));

        // Error 404 para RNC (usando RNC válido)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-consultar-datos-generales-cliente-juridico"))
            .withRequestBody(containing("\"number\":\"404404404\""))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 404,
                        "responseMessage": "RNC no encontrado"
                      },
                      "body": {
                        "message": "El RNC no existe en el sistema"
                      }
                    }
                """)));

        // Error 500 para RNC (usando RNC válido)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-consultar-datos-generales-cliente-juridico"))
            .withRequestBody(containing("\"number\":\"500500500\""))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 500,
                        "responseMessage": "Error interno en servicio juridico"
                      },
                      "body": {
                        "message": "Error interno del servidor"
                      }
                    }
                """)));
    }

    private void configureMasterDataMock() {
        // Mock exitoso para cédula
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consultar-datos-maestro-cedulados"))
            .willReturn(okJson("""
                {
                  "header": {
                    "responseCode": 200,
                    "responseMessage": "Exitoso"
                  },
                  "body": {
                    "code": "000",
                    "message": "Encontrado",
                    "clients": [{
                      "identifications": [{"number": "22500530872", "type": "Cedula"}],
                      "names": "CESAR ARGENIS",
                      "firstName": "RAMIREZ",
                      "middleLastName": "CRISOTOMO",
                      "dateOfBirth": "1990-07-13T04:00:00.000Z",
                      "sex": "M",
                      "photo": "dGVzdEJpbmFyeVBob3Rv"
                    }]
                  }
                }
            """)));

        // Mock para código 904 (cliente no encontrado - activa flujo JCE dinámico)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consultar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"90490490490\""))
            .willReturn(okJson("""
                {
                  "header": {
                    "responseCode": 200,
                    "responseMessage": "Cliente no encontrado"
                  },
                  "body": {
                    "code": "904",
                    "message": "Cliente no encontrado en datos maestros",
                    "clients": []
                  }
                }
            """)));

        // Error 401 para Master Data (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consultar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"40140140140\""))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 401,
                        "responseMessage": "No autorizado"
                      },
                      "body": {
                        "message": "Token invalido para datos maestros"
                      }
                    }
                """)));

        // Error 404 para Master Data (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consultar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"40440440440\""))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 404,
                        "responseMessage": "Servicio no encontrado"
                      },
                      "body": {
                        "message": "Endpoint de datos maestros no disponible"
                      }
                    }
                """)));

        // Error 500 para Master Data (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consultar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"50050050050\""))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 500,
                        "responseMessage": "Error interno en datos maestros"
                      },
                      "body": {
                        "message": "Error interno del servidor de datos maestros"
                      }
                    }
                """)));
    }

    private void configureJceMock() {
        // Mock exitoso para JCE
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-jce"))
            .withRequestBody(containing("\"number\":\"22500530872\""))
            .willReturn(okJson("""
                {
                  "header": {
                    "responseCode": 200,
                    "responseMessage": "Consulta exitosa"
                  },
                  "body": {
                    "code": "000",
                    "message": "Datos encontrados",
                    "clients": [{
                      "identifications": [{"number": "22500530872", "type": "Cedula"}],
                      "names": "CESAR ARGENIS",
                      "firstSurname": "RAMIREZ",
                      "secondSurname": "CRISOTOMO",
                      "birthDate": "1990-07-13T04:00:00.000Z",
                      "gender": "M",
                      "photoBinary": "dGVzdEpDRUJpbmFyeVBob3Rv"
                    }]
                  }
                }
            """)));

        // Mock para activar flujo después de 904
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-jce"))
            .withRequestBody(containing("\"number\":\"90490490490\""))
            .willReturn(okJson("""
                {
                  "header": {
                    "responseCode": 200,
                    "responseMessage": "Consulta post-904"
                  },
                  "body": {
                    "code": "000",
                    "message": "Datos JCE encontrados",
                    "clients": [{
                      "identifications": [{"number": "90490490490", "type": "Cedula"}],
                      "names": "USUARIO POST 904",
                      "firstSurname": "DINAMICO",
                      "photoBinary": "dGVzdFBvc3Q5MDQ="
                    }]
                  }
                }
            """)));

        // Error 401 para JCE (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-jce"))
            .withRequestBody(containing("\"number\":\"40140140140\""))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 401,
                        "responseMessage": "No autorizado"
                      },
                      "body": {
                        "message": "Token invalido para JCE"
                      }
                    }
                """)));

        // Error 404 para JCE (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-jce"))
            .withRequestBody(containing("\"number\":\"40440440440\""))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 404,
                        "responseMessage": "Cedula no encontrada en JCE"
                      },
                      "body": {
                        "message": "La cedula no existe en el sistema JCE"
                      }
                    }
                """)));

        // Error 500 para JCE (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-jce"))
            .withRequestBody(containing("\"number\":\"50050050050\""))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 500,
                        "responseMessage": "Error interno en JCE"
                      },
                      "body": {
                        "message": "Error interno del servidor JCE"
                      }
                    }
                """)));
    }

    private void configureUpdateMasterMock() {
        // Mock exitoso para actualización
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-actualizar-datos-maestro-cedulados"))
            .willReturn(okJson("""
                {
                  "header": {
                    "responseCode": 200,
                    "responseMessage": "TRANSACCION PROCESADA"
                  },
                  "body": {
                    "code": "000",
                    "message": "Datos actualizados exitosamente"
                  }
                }
            """)));

        // Error 401 para actualización (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-actualizar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"40140140140\""))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 401,
                        "responseMessage": "No autorizado"
                      },
                      "body": {
                        "message": "Token invalido para actualizacion"
                      }
                    }
                """)));

        // Error 404 para actualización (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-actualizar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"40440440440\""))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 404,
                        "responseMessage": "Servicio de actualizacion no encontrado"
                      },
                      "body": {
                        "message": "Endpoint de actualizacion no disponible"
                      }
                    }
                """)));

        // Error 500 para actualización (usando cédula válida)
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/ms-actualizar-datos-maestro-cedulados"))
            .withRequestBody(containing("\"number\":\"50050050050\""))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "header": {
                        "responseCode": 500,
                        "responseMessage": "Error interno en actualizacion"
                      },
                      "body": {
                        "message": "Error interno del servidor de actualizacion"
                      }
                    }
                """)));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}