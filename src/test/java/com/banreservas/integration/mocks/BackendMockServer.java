package com.banreservas.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BackendMockServer {
    
    private static final Logger logger = LoggerFactory.getLogger(BackendMockServer.class);
    private static WireMockServer wireMockServer;
    private static final int MOCK_PORT = 8090;

    public static void startServer() {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(MOCK_PORT));
            wireMockServer.start();
            configureFor("localhost", MOCK_PORT);
            setupMockResponses();
            logger.info("WireMock server iniciado en puerto {}", MOCK_PORT);
        }
    }

    public static void stopServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            wireMockServer = null;
            logger.info("WireMock server detenido");
        }
    }

    private static void setupMockResponses() {
        // UN SOLO MOCK QUE RESPONDE A TODO - igual que el ejemplo
        stubFor(any(anyUrl())
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
                            },
                            "code": "000",
                            "message": "Transacción Procesada",
                            "clients": [
                                {
                                    "identifications": [
                                        {
                                            "number": "22500530872",
                                            "type": "Cedula"
                                        }
                                    ],
                                    "names": "CESAR ARGENIS",
                                    "firstName": "CESAR",
                                    "firstSurname": "RAMIREZ",
                                    "secondSurname": "CRISOTOMO",
                                    "birthDate": "1990-07-13T04:00:00.000Z",
                                    "birthPlace": "SABANA GRANDE DE BOYA, R.D.",
                                    "gender": "M",
                                    "maritalStatus": "S",
                                    "categoryId": "0",
                                    "category": "Mayor de Edad",
                                    "photoBinary": "dGVzdEJpbmFyeVBob3Rv"
                                }
                            ]
                        }
                    }
                    """)));

        logger.info("Mock configurado - responde a CUALQUIER request");
    }

    public static void reset() {
        if (wireMockServer != null) {
            wireMockServer.resetAll();
            setupMockResponses();
        }
    }
}