package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Processor para mapear las respuestas backend al formato MICM.
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
        log.info("Mapeando respuestas backend a formato MICM");

        ObjectNode micmResponse = mapper.createObjectNode();
        
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
            log.info("*** DEBUG: - callActualizarDatosMaestro: {}, actualizarResponse: {}", 
                     exchange.getProperty("callActualizarDatosMaestro"), 
                     exchange.getProperty("actualizarResponse") != null);
            
            if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJuridico")) && 
                exchange.getProperty("juridicoResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosGeneralesClienteJuridico");
                mapConsultarDatosJuridicoResponse(micmResponse, exchange.getProperty("juridicoResponse", String.class));
                
            } else if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosMaestro")) && 
                       exchange.getProperty("maestroResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosMaestroCedulados");
                mapConsultarDatosMaestroResponse(micmResponse, exchange.getProperty("maestroResponse", String.class));
                
            } else if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJCE")) && 
                       exchange.getProperty("jceResponse") != null) {
                
                log.info("*** DEBUG: Mapeando respuesta de ConsultarDatosJCE");
                mapConsultarDatosJCEResponse(micmResponse, exchange.getProperty("jceResponse", String.class));
                
            } else {
                // No se ejecutó ningún servicio o no hay respuesta
                log.warn("No se ejecutó ningún servicio o no hay respuesta disponible");
                createErrorResponse(micmResponse, "No se pudo procesar la consulta según las condiciones evaluadas");
                exchange.getIn().setBody(micmResponse.toString());
                return;
            }
            
            log.info("Respuesta MICM mapeada exitosamente");
            
        } catch (Exception e) {
            log.error("Error mapeando respuesta backend", e);
            createErrorResponse(micmResponse, "Error procesando respuesta: " + e.getMessage());
        }
        
        exchange.getIn().setBody(micmResponse.toString());
    }
    
    /**
     * Mapea respuesta de ConsultarDatosGeneralesClienteJuridico.
     */
    private void mapConsultarDatosJuridicoResponse(ObjectNode micmResponse, String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("client")) {
            JsonNode client = body.get("client");
            
            // Mapear identificación
            if (client.has("identification")) {
                JsonNode identification = client.get("identification");
                micmResponse.put("NumeroIdentificacion", identification.path("number").asText());
                micmResponse.put("TipoIdentificacion", identification.path("type").asText());
            }
            
            // Mapear datos del cliente jurídico
            micmResponse.put("Nombres", client.path("businessName").asText());
            micmResponse.put("PrimerApellido", ""); // No aplica para personas jurídicas
            micmResponse.put("SegundoApellido", ""); // No aplica para personas jurídicas
            micmResponse.put("FechaNacimiento", "0001-01-01T00:00:00"); // No aplica
            micmResponse.put("LugarNacimiento", ""); // No aplica
            micmResponse.put("CedulaVieja", ""); // No aplica
            micmResponse.put("Sexo", ""); // No aplica
            micmResponse.put("EstadoCivil", ""); // No aplica
            micmResponse.put("Categoria", ""); // No aplica
            micmResponse.put("CausaInhabilidad", ""); // No aplica
            micmResponse.put("CodigoCausaCancelacion", ""); // No aplica
            micmResponse.put("Estatus", ""); // No aplica
            micmResponse.put("FechaCancelacion", "0001-01-01T00:00:00"); // No aplica
            micmResponse.put("FotoUrl", ""); // No aplica
            micmResponse.put("FotoBinario", ""); // No aplica
            
            log.debug("Cliente jurídico mapeado: {}", client.path("businessName").asText());
        }
    }
    
    /**
     * Mapea respuesta de ConsultarDatosMaestroCedulados.
     */
    private void mapConsultarDatosMaestroResponse(ObjectNode micmResponse, String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("clients") && body.get("clients").isArray() && 
            body.get("clients").size() > 0) {
            
            JsonNode client = body.get("clients").get(0); // Primer cliente
            
            // Mapear identificación
            if (client.has("identifications") && client.get("identifications").isArray() && 
                client.get("identifications").size() > 0) {
                JsonNode identification = client.get("identifications").get(0);
                micmResponse.put("NumeroIdentificacion", identification.path("number").asText());
                micmResponse.put("TipoIdentificacion", identification.path("type").asText());
            }
            
            // Mapear datos del cliente
            micmResponse.put("Nombres", client.path("names").asText());
            micmResponse.put("PrimerApellido", client.path("firstName").asText());
            micmResponse.put("SegundoApellido", client.path("middleLastName").asText());
            
            String birthDate = client.path("dateOfBirth").asText();
            micmResponse.put("FechaNacimiento", birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate);
            
            micmResponse.put("LugarNacimiento", client.path("placeOfBirth").asText());
            micmResponse.put("CedulaVieja", ""); // No disponible en este servicio
            micmResponse.put("Sexo", client.path("sex").asText());
            micmResponse.put("EstadoCivil", client.path("maritalStatus").asText());
            micmResponse.put("Categoria", client.path("category").asText());
            micmResponse.put("CausaInhabilidad", client.path("cancellationCause").asText());
            micmResponse.put("CodigoCausaCancelacion", client.path("cancellationCauseID").asText());
            micmResponse.put("Estatus", client.path("stateID").asText());
            
            String cancellationDate = client.path("cancellationDate").asText();
            micmResponse.put("FechaCancelacion", cancellationDate.isEmpty() ? "0001-01-01T00:00:00" : cancellationDate);
            
            micmResponse.put("FotoUrl", ""); // No disponible en este servicio
            micmResponse.put("FotoBinario", client.path("photo").asText(""));
            
            log.debug("Cliente maestro mapeado: {}", client.path("names").asText());
        }
    }
    
    /**
     * Mapea respuesta de ConsultarDatosJCE.
     */
    private void mapConsultarDatosJCEResponse(ObjectNode micmResponse, String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
        
        if (body != null && body.has("clients") && body.get("clients").isArray() && 
            body.get("clients").size() > 0) {
            
            JsonNode client = body.get("clients").get(0); // Primer cliente
            
            // Mapear identificación
            if (client.has("identifications") && client.get("identifications").isArray() && 
                client.get("identifications").size() > 0) {
                JsonNode identification = client.get("identifications").get(0);
                micmResponse.put("NumeroIdentificacion", identification.path("number").asText());
                micmResponse.put("TipoIdentificacion", identification.path("type").asText());
            }
            
            // Mapear datos del cliente JCE
            micmResponse.put("Nombres", client.path("names").asText());
            micmResponse.put("PrimerApellido", client.path("firstSurname").asText());
            micmResponse.put("SegundoApellido", client.path("secondSurname").asText());
            
            String birthDate = client.path("birthDate").asText();
            micmResponse.put("FechaNacimiento", birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate);
            
            micmResponse.put("LugarNacimiento", client.path("birthPlace").asText());
            micmResponse.put("CedulaVieja", ""); // No disponible en JCE
            micmResponse.put("Sexo", client.path("gender").asText());
            micmResponse.put("EstadoCivil", client.path("maritalStatus").asText());
            micmResponse.put("Categoria", client.path("category").asText());
            micmResponse.put("CausaInhabilidad", client.path("cancelReason").asText());
            micmResponse.put("CodigoCausaCancelacion", client.path("cancelReasonId").asText());
            micmResponse.put("Estatus", client.path("stateId").asText());
            
            String cancelDate = client.path("cancelDate").asText();
            micmResponse.put("FechaCancelacion", cancelDate.isEmpty() ? "0001-01-01T00:00:00" : cancelDate);
            
            micmResponse.put("FotoUrl", ""); // No disponible en JCE
            micmResponse.put("FotoBinario", ""); // No disponible en JCE
            
            log.debug("Cliente JCE mapeado: {}", client.path("names").asText());
        }
    }
    
    /**
     * Crea una respuesta de error en formato MICM.
     */
    private void createErrorResponse(ObjectNode micmResponse, String errorMessage) {
        micmResponse.put("NumeroIdentificacion", "");
        micmResponse.put("TipoIdentificacion", "");
        micmResponse.put("Nombres", "");
        micmResponse.put("PrimerApellido", "");
        micmResponse.put("SegundoApellido", "");
        micmResponse.put("FechaNacimiento", "0001-01-01T00:00:00");
        micmResponse.put("LugarNacimiento", "");
        micmResponse.put("CedulaVieja", "");
        micmResponse.put("Sexo", "");
        micmResponse.put("EstadoCivil", "");
        micmResponse.put("Categoria", "");
        micmResponse.put("CausaInhabilidad", "");
        micmResponse.put("CodigoCausaCancelacion", "");
        micmResponse.put("Estatus", "");
        micmResponse.put("FechaCancelacion", "0001-01-01T00:00:00");
        micmResponse.put("FotoUrl", "");
        micmResponse.put("FotoBinario", "");
        micmResponse.put("Error", errorMessage);
        
        log.warn("Respuesta de error creada: {}", errorMessage);
    }
}