package com.banreservas.integration.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.model.inbound.GetClientGeneralDataResponse;

class BackendResponseMappingProcessorTest {

    private BackendResponseMappingProcessor processor;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        processor = new BackendResponseMappingProcessor();
        exchange = new DefaultExchange(new DefaultCamelContext());
}

    @Test
    void shouldMapJceResponseWithHighestPriority() throws Exception {
        String jceResponse = """
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
                            "cancelDate": "2024-01-01T00:00:00",
                            "photoBinary": "/9j/4AAQSkZJRgABAQEAYABgAAD/"
                        }
                    ]
                }
            }
            """;

        exchange.setProperty("callJceService", true);
        exchange.setProperty("jceResponse", jceResponse);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("40233832993", response.identificacion().identificationNumber());
        assertEquals("Cedula", response.identificacion().identificationType());
        assertEquals("DOMINGO JUNIOR", response.names());
        assertEquals("RUIZ", response.firstLastName());
        assertEquals("MELENCIANO", response.secondLastName());
        assertEquals("/9j/4AAQSkZJRgABAQEAYABgAAD/", response.photoBinary());
    }

    @Test
    void shouldMapMasterDataResponseWhenJceNotAvailable() throws Exception {
        String masterResponse = """
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
                            "cancellationDate": "2024-01-01T00:00:00",
                            "photo": "photo_data_here"
                        }
                    ]
                }
            }
            """;

        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("masterDataResponse", masterResponse);
        exchange.setProperty("clientNotFoundInMaster", false);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("40233832993", response.identificacion().identificationNumber());
        assertEquals("Cedula", response.identificacion().identificationType());
        assertEquals("DOMINGO JUNIOR", response.names());
        assertEquals("DOMINGO", response.firstLastName());
        assertEquals("RUIZ MELENCIANO", response.secondLastName());
        assertEquals("photo_data_here", response.photoBinary());
    }

    @Test
    void shouldMapLegalClientResponseWhenOthersNotAvailable() throws Exception {
        String legalResponse = """
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
            """;

        exchange.setProperty("callLegalClientService", true);
        exchange.setProperty("legalClientResponse", legalResponse);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("101199662", response.identificacion().identificationNumber());
        assertEquals("RNC", response.identificacion().identificationType());
        assertEquals("BON AGROINDUSTRIAL", response.names());
        assertEquals("", response.firstLastName());
        assertEquals("", response.secondLastName());
    }

    @Test
    void shouldPrioritizeJceOverMasterData() throws Exception {
        String jceResponse = """
            {
                "header": {"responseCode": 200, "responseMessage": "Consulta exitosa"},
                "body": {
                    "clients": [{
                        "identifications": [{"number": "40233832993", "type": "Cedula"}],
                        "names": "JCE_NAME",
                        "firstSurname": "JCE_SURNAME",
                        "secondSurname": "JCE_SECOND",
                        "birthDate": "2003-02-21T05:00:00.000Z",
                        "cancelDate": "2024-01-01T00:00:00"
                    }]
                }
            }
            """;

        String masterResponse = """
            {
                "header": {"responseCode": 200, "responseMessage": "Exitoso"},
                "body": {
                    "code": "000",
                    "clients": [{
                        "identifications": [{"number": "40233832993", "type": "Cedula"}],
                        "names": "MASTER_NAME",
                        "firstName": "MASTER_SURNAME"
                    }]
                }
            }
            """;

        exchange.setProperty("callJceService", true);
        exchange.setProperty("jceResponse", jceResponse);
        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("masterDataResponse", masterResponse);
        exchange.setProperty("clientNotFoundInMaster", false);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("JCE_NAME", response.names());
        assertEquals("JCE_SURNAME", response.firstLastName());
        assertEquals("JCE_SECOND", response.secondLastName());
    }

    @Test
    void shouldSkipMasterDataWhenClientNotFound() throws Exception {
        String legalResponse = """
            {
                "header": {"responseCode": 200, "responseMessage": "Exitoso"},
                "body": {
                    "client": {
                        "identification": {"number": "101199662", "type": "RNC"},
                        "businessName": "LEGAL_CLIENT_NAME"
                    }
                }
            }
            """;

        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("masterDataResponse", "some_response");
        exchange.setProperty("clientNotFoundInMaster", true);
        exchange.setProperty("callLegalClientService", true);
        exchange.setProperty("legalClientResponse", legalResponse);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("LEGAL_CLIENT_NAME", response.names());
    }

    @Test
    void shouldHandleEmptyBirthDate() throws Exception {
        String masterResponse = """
            {
                "header": {"responseCode": 200, "responseMessage": "Exitoso"},
                "body": {
                    "code": "000",
                    "clients": [{
                        "identifications": [{"number": "40233832993", "type": "Cedula"}],
                        "names": "TEST_NAME",
                        "dateOfBirth": "",
                        "cancellationDate": ""
                    }]
                }
            }
            """;

        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("masterDataResponse", masterResponse);
        exchange.setProperty("clientNotFoundInMaster", false);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("0001-01-01T00:00:00", response.birthDate());
        assertEquals("0001-01-01T00:00:00", response.cancellationDate());
    }

    @Test
    void shouldHandleEmptyJceDates() throws Exception {
        String jceResponse = """
            {
                "header": {"responseCode": 200, "responseMessage": "Consulta exitosa"},
                "body": {
                    "clients": [{
                        "identifications": [{"number": "40233832993", "type": "Cedula"}],
                        "names": "TEST_NAME",
                        "birthDate": "",
                        "cancelDate": ""
                    }]
                }
            }
            """;

        exchange.setProperty("callJceService", true);
        exchange.setProperty("jceResponse", jceResponse);
        
        processor.process(exchange);
        
        GetClientGeneralDataResponse response = exchange.getIn().getBody(GetClientGeneralDataResponse.class);
        
        assertEquals("0001-01-01T00:00:00", response.birthDate());
        assertEquals("0001-01-01T00:00:00", response.cancellationDate());
    }

    @Test
    void shouldThrowExceptionWhenNoValidResponseFound() {
        exchange.setProperty("callLegalClientService", false);
        exchange.setProperty("callMasterDataService", false);
        exchange.setProperty("callJceService", false);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> processor.process(exchange));
        assertEquals("No se encontró respuesta válida de ningún servicio backend", exception.getMessage());
    }

    @Test
    void shouldHandleNullResponses() {
        exchange.setProperty("callJceService", true);
        exchange.setProperty("jceResponse", null);
        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("masterDataResponse", null);
        exchange.setProperty("callLegalClientService", true);
        exchange.setProperty("legalClientResponse", null);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> processor.process(exchange));
        assertEquals("No se encontró respuesta válida de ningún servicio backend", exception.getMessage());
    }
}