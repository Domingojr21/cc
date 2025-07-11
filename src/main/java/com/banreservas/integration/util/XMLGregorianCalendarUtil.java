package com.banreservas.integration.util;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLGregorianCalendarUtil {
    
    public static XMLGregorianCalendar parseToXMLGregorianCalendar(String dateString) {
        if (dateString == null || dateString.trim().isEmpty() || "0001-01-01".equals(dateString)) {
            return null;
        }
        
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        } catch (Exception e) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(dateString + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
            } catch (Exception ex) {
                return null;
            }
        }
    }
}