package com.banreservas.integration.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;

public class XMLGregorianCalendarDeserializer extends JsonDeserializer<XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String dateString = p.getText();

        // Convertir la cadena a XMLGregorianCalendar usando DatatypeFactory
        DatatypeFactory df;
        XMLGregorianCalendar xmlCalendar = null;
        try {
            df = DatatypeFactory.newInstance();
            xmlCalendar = df.newXMLGregorianCalendar(dateString);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return xmlCalendar;
    }
}