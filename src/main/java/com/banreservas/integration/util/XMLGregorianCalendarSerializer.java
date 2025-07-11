package com.banreservas.integration.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;

public class XMLGregorianCalendarSerializer extends JsonSerializer<XMLGregorianCalendar> {

    @Override
    public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        // Convertir XMLGregorianCalendar a cadena en formato ISO 8601
        String isoString = value.toXMLFormat();
        gen.writeString(isoString);
    }
}