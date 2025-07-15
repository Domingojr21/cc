package com.banreservas.integration.processors;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.banreservas.integration.util.Constants;

class OrchestrationDecisionProcessorTest {

    private OrchestrationDecisionProcessor processor;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        processor = new OrchestrationDecisionProcessor();
        exchange = new DefaultExchange(new DefaultCamelContext());
    }

    @Test
    void shouldCallLegalClientServiceForRNC() throws Exception {
        exchange.setProperty("identificationTypeRq", "RNC");
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        assertTrue((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldCallMasterDataServiceForCedulaWithoutForceUpdate() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertTrue((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldCallJceServiceForCedulaWithForceUpdate() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", "TRUE");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertTrue((Boolean) exchange.getProperty("callJceService"));
        assertTrue((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldCallJceAndUpdateMasterWhenForceUpdateIsTrue() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", "TRUE");
        
        processor.process(exchange);
        
        assertTrue((Boolean) exchange.getProperty("callJceService"));
        assertTrue((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldNotCallAnyServiceForUnknownIdentificationType() throws Exception {
        exchange.setProperty("identificationTypeRq", "Unknown");
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldHandleCaseInsensitiveIdentificationType() throws Exception {
        exchange.setProperty("identificationTypeRq", "cedula");
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldHandleCaseInsensitiveForceUpdate() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", "true");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldCallCorrectServicesForRNCRegardlessOfForceUpdate() throws Exception {
        exchange.setProperty("identificationTypeRq", "RNC");
        exchange.setProperty("forceUpdateRq", "TRUE");
        
        processor.process(exchange);
        
        assertTrue((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldHandleNullIdentificationType() throws Exception {
        exchange.setProperty("identificationTypeRq", null);
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldHandleNullForceUpdate() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", null);
        
        processor.process(exchange);
        
        assertFalse((Boolean) exchange.getProperty("callLegalClientService"));
        assertFalse((Boolean) exchange.getProperty("callMasterDataService"));
        assertFalse((Boolean) exchange.getProperty("callJceService"));
        assertFalse((Boolean) exchange.getProperty("callUpdateMasterService"));
    }

    @Test
    void shouldValidateAtLeastOneServiceExecution() throws Exception {
        exchange.setProperty("identificationTypeRq", "Cedula");
        exchange.setProperty("forceUpdateRq", "FALSE");
        
        processor.process(exchange);
        
        boolean atLeastOneService = (Boolean) exchange.getProperty("callLegalClientService") ||
                                  (Boolean) exchange.getProperty("callMasterDataService") ||
                                  (Boolean) exchange.getProperty("callJceService");
        assertTrue(atLeastOneService);
    }
}