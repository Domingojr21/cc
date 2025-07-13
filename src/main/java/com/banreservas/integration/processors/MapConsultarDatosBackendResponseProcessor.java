package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.inbound.ConsultarDatosGeneralesClienteInboundResponse;
import com.banreservas.integration.model.inbound.ConsultarDatosGeneralesClienteInboundResponse.Identificacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para mapear las respuestas backend al formato MICM usando type safety.
 * 
 * @author Jenrry Monegro - c-jmonegro@banreservas.com
 * @since 04/07/2025
 * @version 1.0.0
 */
@ApplicationScoped
@RegisterForReflection
public class MapConsultarDatosBackendResponseProcessor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(MapConsultarDatosBackendResponseProcessor.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Mapeando respuestas backend a formato MICM con type safety");

        ConsultarDatosGeneralesClienteInboundResponse response = null;
        
        // Determinar qué servicio se ejecutó y mapear su respuesta
        try {
            log.info("*** DEBUG: Verificando respuestas disponibles:");
            log.info("*** DEBUG: - callConsultarDatosJuridico: {}, juridicoResponse: {}", 
                     exchange.getProperty("callConsultarDatosJuridico"), 
                     exchange.getProperty("juridicoResponse") != null);
            log.info("*** DEBUG: - callConsultarDatosMaestro: {}, maestroResponse: {}", 
                     exchange.getProperty("callConsultarDatosMaestro"), 
                     exchange.getProperty("maestroResponse") != null);
            log.info("*** DEBUG: - callConsultarDatosJCE: {}, jceResponse: {}", 
                     exchange.getProperty("callConsultarDatosJCE"), 
                     exchange.getProperty("jceResponse") != null);
            
            if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJuridico")) && 
                exchange.getProperty("juridicoResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosGeneralesClienteJuridico");
                response = mapConsultarDatosJuridicoResponse(exchange.getProperty("juridicoResponse", String.class));
                
            } else if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosMaestro")) && 
                       exchange.getProperty("maestroResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosMaestroCedulados");
                response = mapConsultarDatosMaestroResponse(exchange.getProperty("maestroResponse", String.class));
                
            } else if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJCE")) && 
                       exchange.getProperty("jceResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosJCE");
                response = mapConsultarDatosJCEResponse(exchange.getProperty("jceResponse", String.class));
                
            } else {
                // No se ejecutó ningún servicio o no hay respuesta
                log.warn("No se ejecutó ningún servicio o no hay respuesta disponible");
                response = createErrorResponse("No se pudo procesar la consulta según las condiciones evaluadas");
            }
            
            // Convertir el objeto response directamente a JSON sin doble serialización
            exchange.getIn().setBody(response);
            
            log.info("Respuesta MICM mapeada exitosamente con type safety");
            
        } catch (Exception e) {
            log.error("Error mapeando respuesta backend", e);
            response = createErrorResponse("Error procesando respuesta: " + e.getMessage());
            exchange.getIn().setBody(response);
        }
    }
    
    /**
     * Mapea respuesta de ConsultarDatosGeneralesClienteJuridico.
     */
    private ConsultarDatosGeneralesClienteInboundResponse mapConsultarDatosJuridicoResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("client")) {
            JsonNode client = body.get("client");
            
            // Crear identificación
            Identificacion identificacion = null;
            if (client.has("identification")) {
                JsonNode identification = client.get("identification");
                identificacion = new Identificacion(
                    identification.path("number").asText(),
                    identification.path("type").asText()
                );
            } else {
                identificacion = new Identificacion("", "");
            }
            
            // Mapear datos del cliente jurídico
            return new ConsultarDatosGeneralesClienteInboundResponse(
                identificacion,
                client.path("businessName").asText(),
                "", // No aplica para personas jurídicas
                "", // No aplica para personas jurídicas
                "0001-01-01T00:00:00", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "", // No aplica
                "0001-01-01T00:00:00", // No aplica
                "", // No aplica
                "" // No aplica
            );
        }
        
        return createErrorResponse("No se encontraron datos del cliente jurídico");
    }
    
    /**
     * Mapea respuesta de ConsultarDatosMaestroCedulados.
     */
    private ConsultarDatosGeneralesClienteInboundResponse mapConsultarDatosMaestroResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("clients") && body.get("clients").isArray() && 
            body.get("clients").size() > 0) {
            
            JsonNode client = body.get("clients").get(0); // Primer cliente
            
            // Crear identificación
            Identificacion identificacion = null;
            if (client.has("identifications") && client.get("identifications").isArray() && 
                client.get("identifications").size() > 0) {
                JsonNode identification = client.get("identifications").get(0);
                identificacion = new Identificacion(
                    identification.path("number").asText(),
                    identification.path("type").asText()
                );
            } else {
                identificacion = new Identificacion("", "");
            }
            
            // Mapear datos del cliente
            String birthDate = client.path("dateOfBirth").asText();
            String cancellationDate = client.path("cancellationDate").asText();
            
            return new ConsultarDatosGeneralesClienteInboundResponse(
                identificacion,
                client.path("names").asText(),
                client.path("firstName").asText(),
                client.path("middleLastName").asText(),
                birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
                client.path("placeOfBirth").asText(),
                "", // No disponible en este servicio
                client.path("sex").asText(),
                client.path("maritalStatus").asText(),
                client.path("category").asText(),
                client.path("cancellationCause").asText(),
                client.path("cancellationCauseID").asText(),
                client.path("stateID").asText(),
                cancellationDate.isEmpty() ? "0001-01-01T00:00:00" : cancellationDate,
                "", // No disponible en este servicio
                client.path("photo").asText("")
            );
        }
        
        return createErrorResponse("No se encontraron datos del cliente en maestro");
    }
    
    /**
     * Mapea respuesta de ConsultarDatosJCE.
     */
    private ConsultarDatosGeneralesClienteInboundResponse mapConsultarDatosJCEResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("clients") && body.get("clients").isArray() && 
            body.get("clients").size() > 0) {
            
            JsonNode client = body.get("clients").get(0); // Primer cliente
            
            // Crear identificación
            Identificacion identificacion = null;
            if (client.has("identifications") && client.get("identifications").isArray() && 
                client.get("identifications").size() > 0) {
                JsonNode identification = client.get("identifications").get(0);
                identificacion = new Identificacion(
                    identification.path("number").asText(),
                    identification.path("type").asText()
                );
            } else {
                identificacion = new Identificacion("", "");
            }
            
            // Mapear datos del cliente JCE
            String birthDate = client.path("birthDate").asText();
            String cancelDate = client.path("cancelDate").asText();
            
            return new ConsultarDatosGeneralesClienteInboundResponse(
                identificacion,
                client.path("names").asText(),
                client.path("firstSurname").asText(),
                client.path("secondSurname").asText(),
                birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
                client.path("birthPlace").asText(),
                "", // No disponible en JCE
                client.path("gender").asText(),
                client.path("maritalStatus").asText(),
                client.path("category").asText(),
                client.path("cancelReason").asText(),
                client.path("cancelReasonId").asText(),
                client.path("stateId").asText(),
                cancelDate.isEmpty() ? "0001-01-01T00:00:00" : cancelDate,
                "", // No disponible en JCE
                "" // No disponible en JCE
            );
        }
        
        return createErrorResponse("No se encontraron datos del cliente en JCE");
    }
    
    /**
     * Crea una respuesta de error en formato MICM.
     */
    private ConsultarDatosGeneralesClienteInboundResponse createErrorResponse(String errorMessage) {
        Identificacion identificacion = new Identificacion("", "");
        
        // Nota: El record no tiene campo "error", se manejará en el ErrorResponseProcessor
        return new ConsultarDatosGeneralesClienteInboundResponse(
            identificacion,
            "",
            "",
            "",
            "0001-01-01T00:00:00",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "0001-01-01T00:00:00",
            "",
            ""
        );
    }
}