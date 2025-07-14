package com.banreservas.integration.util;

public class Constants {
    
    // HTTP Response Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_INTERNAL_ERROR = 500;
    
    // Error Codes
    public static final String ERROR_CODE_SUCCESS = "000";
    public static final String ERROR_CODE_NOT_FOUND = "904";
    public static final String ERROR_CODE_INTERNAL_ERROR = "500";
    public static final String ERROR_CODE_BAD_REQUEST = "400";
    
    // Identification Types
    public static final String IDENTIFICATION_TYPE_CEDULA = "Cedula";
    public static final String IDENTIFICATION_TYPE_RNC = "RNC";
    
    // Boolean Values
    public static final String BOOLEAN_TRUE = "TRUE";
    public static final String BOOLEAN_FALSE = "FALSE";
    
    // Error Messages
    public static final String ERROR_MESSAGE_CLIENT_NOT_FOUND = "Cliente no encontrado en datos maestros";
    public static final String ERROR_MESSAGE_INVALID_IDENTIFICATION_TYPE = "Tipo de identificación inválido";
    public static final String ERROR_MESSAGE_IDENTIFICATION_REQUIRED = "Número de identificación es requerido";
    public static final String ERROR_MESSAGE_SERVICE_UNAVAILABLE = "Servicio no disponible temporalmente";
    public static final String ERROR_MESSAGE_INTERNAL_ERROR = "Error interno del servidor";
    
    // Response Messages
    public static final String RESPONSE_MESSAGE_SUCCESS = "Transacción procesada exitosamente";
    public static final String RESPONSE_MESSAGE_ERROR = "Error en el procesamiento";
    
    // Request Validation Messages
    public static final String VALIDATION_MESSAGE_IDENTIFICATION_REQUIRED = "Número de identificación es requerido";
    public static final String VALIDATION_MESSAGE_IDENTIFICATION_TYPE_REQUIRED = "Tipo de identificación es requerido";
    public static final String VALIDATION_MESSAGE_INVALID_IDENTIFICATION_TYPE = "Tipo de identificación debe ser: Cedula o RNC";
    public static final String VALIDATION_MESSAGE_INVALID_FORCE_UPDATE = "ForzarActualizar debe ser TRUE o FALSE";
    public static final String VALIDATION_MESSAGE_INVALID_INCLUDE_BINARY_PHOTO = "IncluirFotoBinaria debe ser TRUE o FALSE";
    
    // Headers
    public static final String HEADER_CANAL = "Canal";
    public static final String HEADER_USUARIO = "Usuario";
    public static final String HEADER_TERMINAL = "Terminal";
    public static final String HEADER_FECHA_HORA = "FechaHora";
    public static final String HEADER_VERSION = "Version";
    public static final String HEADER_SERVICIO = "Servicio";
    public static final String HEADER_SESSION_ID = "sessionId";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    
    // Default Values
    public static final String DEFAULT_FORCE_UPDATE = BOOLEAN_FALSE;
    public static final String DEFAULT_INCLUDE_BINARY_PHOTO = BOOLEAN_FALSE;
    public static final String DEFAULT_CANAL = "MICM";
    public static final String DEFAULT_VERSION = "1";
    public static final String DEFAULT_SERVICIO = "ConsultarDatosGeneralesCliente";
    
    // Service Names for Logging
    public static final String SERVICE_CONSULTAR_DATOS_GENERALES_CLIENTE_JURIDICO = "ConsultarDatosGeneralesClienteJuridico";
    public static final String SERVICE_CONSULTAR_DATOS_MAESTRO_CEDULADOS = "ConsultarDatosMaestroCedulados";
    public static final String SERVICE_CONSULTAR_DATOS_JCEDP = "ConsultarDatosJCEDP";
    public static final String SERVICE_ACTUALIZAR_DATOS_MAESTRO_CEDULADOS = "ActualizarDatosMaestroCedulados";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}