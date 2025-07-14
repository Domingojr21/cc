package com.banreservas.integration.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.banreservas.integration.model.inbound.GetClientGeneralDataInboundResponse;
import com.banreservas.integration.model.inbound.GetClientGeneralDataInboundResponse.Identificacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@RegisterForReflection
public class MapGetDataBackendResponseProcessor implements Processor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        GetClientGeneralDataInboundResponse response;

            if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosJCE")) && 
                exchange.getProperty("jceResponse") != null) {
                response = mapConsultarDatosJCEResponse(exchange.getProperty("jceResponse", String.class));
            } else if (Boolean.TRUE.equals(exchange.getProperty("callConsultarDatosMaestro")) && 
                       exchange.getProperty("maestroResponse") != null &&
                       !Boolean.TRUE.equals(exchange.getProperty("clientNotFoundInMaster"))) {
                response = mapConsultarDatosMaestroResponse(exchange.getProperty("maestroResponse", String.class));
            } else {
                response = mapConsultarDatosJuridicoResponse(exchange.getProperty("juridicoResponse", String.class));
            } 

            exchange.getIn().setBody(response);
        
    }

    private GetClientGeneralDataInboundResponse mapConsultarDatosJuridicoResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");

            JsonNode client = body.get("client");
            JsonNode identification = client.path("identification");

            Identificacion identificacion = new Identificacion(
                identification.path("number").textValue() ,
                identification.path("type").textValue() 
            );

            return new GetClientGeneralDataInboundResponse(
                identificacion,
                client.path("businessName").textValue() ,
                "", "", "0001-01-01T00:00:00",
                "", "", "", "", "", "", "", "", "0001-01-01T00:00:00", "", ""
            );
        
    }

    private GetClientGeneralDataInboundResponse mapConsultarDatosMaestroResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");

            JsonNode client = body.get("clients").get(0);
            JsonNode identification = client.path("identifications").get(0);

            Identificacion identificacion = new Identificacion(
                identification.path("number").textValue() ,
                identification.path("type").textValue() 
            );

            String birthDate = client.path("dateOfBirth").textValue() ;
            String cancellationDate = client.path("cancellationDate").textValue() ;

            return new GetClientGeneralDataInboundResponse(
                identificacion,
                client.path("names").textValue() ,
                client.path("firstName").textValue() ,
                client.path("middleLastName").textValue() ,
                birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
                client.path("placeOfBirth").textValue() ,
                "",
                client.path("sex").textValue() ,
                client.path("maritalStatus").textValue() ,
                client.path("category").textValue() ,
                client.path("cancellationCause").textValue() ,
                client.path("cancellationCauseID").textValue() ,
                client.path("stateID").textValue() ,
                cancellationDate.isEmpty() ? "0001-01-01T00:00:00" : cancellationDate,
                "",
                client.path("photo").textValue() 
            );

    }

    private GetClientGeneralDataInboundResponse mapConsultarDatosJCEResponse(String jsonResponse) throws Exception {
        JsonNode response = mapper.readTree(jsonResponse);
        JsonNode body = response.get("body");
 
            JsonNode client = body.get("clients").get(0);
            JsonNode identification = client.path("identifications").get(0);

            Identificacion identificacion = new Identificacion(
                identification.path("number").textValue() ,
                identification.path("type").textValue() 
            );

            String birthDate = client.path("birthDate").textValue() ;
            String cancelDate = client.path("cancelDate").textValue() ;

            return new GetClientGeneralDataInboundResponse(
                identificacion,
                client.path("names").textValue(),
                client.path("firstSurname").textValue() ,
                client.path("secondSurname").textValue() ,
                birthDate.isEmpty() ? "0001-01-01T00:00:00" : birthDate,
                client.path("birthPlace").textValue() ,
                "",
                client.path("gender").textValue() ,
                client.path("maritalStatus").textValue() ,
                client.path("category").textValue() ,
                client.path("cancelReason").textValue() ,
                client.path("cancelReasonId").textValue() ,
                client.path("stateId").textValue() ,
                cancelDate.isEmpty() ? "0001-01-01T00:00:00" : cancelDate,
                "",
                client.path("photoBinary").textValue() 
            );
        
    }
}
