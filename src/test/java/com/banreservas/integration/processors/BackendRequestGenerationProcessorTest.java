package com.banreservas.integration.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.model.outbound.backend.GetClientGeneralDataRequest;
import com.banreservas.integration.model.outbound.backend.GetJCEDataRequest;
import com.banreservas.integration.model.outbound.backend.GetLegalClientGeneralDataRequest;

class BackendRequestGenerationProcessorTest {

    private BackendRequestGenerationProcessor processor;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        processor = new BackendRequestGenerationProcessor();
        exchange = new DefaultExchange(new DefaultCamelContext());
        
        exchange.setProperty("identificationNumberRq", "40233832993");
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("includeBinaryPhotoRq", "TRUE");
    }

    @Test
    void shouldGenerateLegalClientRequestWhenFlagIsSet() throws Exception {
        exchange.setProperty("callLegalClientService", true);
        
        processor.process(exchange);
        
        GetLegalClientGeneralDataRequest request = 
            (GetLegalClientGeneralDataRequest) exchange.getProperty("legalClientRequest");
        
        assertNotNull(request);
        assertEquals("40233832993", request.client().identification().number());
        assertEquals("Cedula", request.client().identification().type());
    }

    @Test
    void shouldNotGenerateLegalClientRequestWhenFlagIsFalse() throws Exception {
        exchange.setProperty("callLegalClientService", false);
        
        processor.process(exchange);
        
        assertNull(exchange.getProperty("legalClientRequest"));
    }

    @Test
    void shouldGenerateMasterDataRequestWhenFlagIsSet() throws Exception {
        exchange.setProperty("callMasterDataService", true);
        
        processor.process(exchange);
        
        GetClientGeneralDataRequest request = 
            (GetClientGeneralDataRequest) exchange.getProperty("masterDataRequest");
        
        assertNotNull(request);
        assertEquals("40233832993", request.clients().get(0).identifications().get(0).number());
        assertEquals("Cedula", request.clients().get(0).identifications().get(0).type());
        assertTrue(request.includeBinaryPhoto());
    }

    @Test
    void shouldNotGenerateMasterDataRequestWhenFlagIsFalse() throws Exception {
        exchange.setProperty("callMasterDataService", false);
        
        processor.process(exchange);
        
        assertNull(exchange.getProperty("masterDataRequest"));
    }

    @Test
    void shouldGenerateJceRequestWhenFlagIsSet() throws Exception {
        exchange.setProperty("callJceService", true);
        
        processor.process(exchange);
        
        GetJCEDataRequest request = 
            (GetJCEDataRequest) exchange.getProperty("jceRequest");
        
        assertNotNull(request);
        assertTrue(request.includeBinaryPhoto());
    }

    @Test
    void shouldNotGenerateJceRequestWhenFlagIsFalse() throws Exception {
        exchange.setProperty("callJceService", false);
        
        processor.process(exchange);
        
        assertNull(exchange.getProperty("jceRequest"));
    }

    @Test
    void shouldGenerateMultipleRequestsWhenMultipleFlagsAreSet() throws Exception {
        exchange.setProperty("callLegalClientService", true);
        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("callJceService", true);
        
        processor.process(exchange);
        
        assertNotNull(exchange.getProperty("legalClientRequest"));
        assertNotNull(exchange.getProperty("masterDataRequest"));
        assertNotNull(exchange.getProperty("jceRequest"));
    }

    @Test
    void shouldHandleIncludeBinaryPhotoFalse() throws Exception {
        exchange.setProperty("includeBinaryPhotoRq", "FALSE");
        exchange.setProperty("callMasterDataService", true);
        exchange.setProperty("callJceService", true);
        
        processor.process(exchange);
        
        GetClientGeneralDataRequest masterRequest = 
            (GetClientGeneralDataRequest) exchange.getProperty("masterDataRequest");
        GetJCEDataRequest jceRequest = 
            (GetJCEDataRequest) exchange.getProperty("jceRequest");
        
        assertFalse(masterRequest.includeBinaryPhoto());
        assertFalse(jceRequest.includeBinaryPhoto());
    }

    @Test
    void shouldGenerateRequestsWithCorrectRNCData() throws Exception {
        exchange.setProperty("identificationNumberRq", "101199662");
        exchange.setProperty("identificationTypeRq", "RNC");
        exchange.setProperty("callLegalClientService", true);
        
        processor.process(exchange);
        
        GetLegalClientGeneralDataRequest request = 
            (GetLegalClientGeneralDataRequest) exchange.getProperty("legalClientRequest");
        
        assertEquals("101199662", request.client().identification().number());
        assertEquals("RNC", request.client().identification().type());
    }

    @Test
    void shouldNotGenerateAnyRequestsWhenNoFlagsAreSet() throws Exception {
        exchange.setProperty("callLegalClientService", false);
        exchange.setProperty("callMasterDataService", false);
        exchange.setProperty("callJceService", false);
        
        processor.process(exchange);
        
        assertNull(exchange.getProperty("legalClientRequest"));
        assertNull(exchange.getProperty("masterDataRequest"));
        assertNull(exchange.getProperty("jceRequest"));
    }

    @Test
    void shouldHandleNullFlags() throws Exception {
        processor.process(exchange);
        
        assertNull(exchange.getProperty("legalClientRequest"));
        assertNull(exchange.getProperty("masterDataRequest"));
        assertNull(exchange.getProperty("jceRequest"));
    }

    @Test
    void shouldHandleMissingParameters() throws Exception {
        Exchange exchangeWithoutParams = new DefaultExchange(new DefaultCamelContext());
        exchangeWithoutParams.setProperty("callMasterDataService", true);
        
        processor.process(exchangeWithoutParams);
        
        GetClientGeneralDataRequest request = 
            (GetClientGeneralDataRequest) exchangeWithoutParams.getProperty("masterDataRequest");
        
        assertNotNull(request);
        assertNull(request.clients().get(0).identifications().get(0).number());
        assertNull(request.clients().get(0).identifications().get(0).type());
        assertFalse(request.includeBinaryPhoto());
    }
}