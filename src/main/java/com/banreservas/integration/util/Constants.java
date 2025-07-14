package com.banreservas.integration.util;

/**
 * Constantes utilizadas en el microservicio de orquestación.
 * Contiene códigos de respuesta, tipos de identificación, valores booleanos y mensajes estándar.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
public final class Constants {
    
    // HTTP Response Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_ERROR = 500;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;
    
    // Error Codes
    public static final String ERROR_CODE_SUCCESS = "000";
    public static final String ERROR_CODE_NOT_FOUND = "904";
    public static final String ERROR_CODE_INTERNAL_ERROR = "500";
    public static final String ERROR_CODE_BAD_REQUEST = "400";
    public static final String ERROR_CODE_UNAUTHORIZED = "401";
    public static final String ERROR_CODE_SERVICE_UNAVAILABLE = "503";
    
    // Identification Types
    public static final String IDENTIFICATION_TYPE_CEDULA = "Cedula";
    public static final String IDENTIFICATION_TYPE_RNC = "RNC";
    public static final String IDENTIFICATION_TYPE_PASSPORT = "Pasaporte";
    public static final String IDENTIFICATION_TYPE_PERMIT = "PermId";
    
    // Boolean Values
    public static final String BOOLEAN_TRUE = "TRUE";
    public static final String BOOLEAN_FALSE = "FALSE";
    
    // Error Messages (Spanish for responses)
    public static final String ERROR_MESSAGE_CLIENT_NOT_FOUND = "Cliente no encontrado en datos maestros";
    public static final String ERROR_MESSAGE_INVALID_IDENTIFICATION_TYPE = "Tipo de identificación inválido";
    public static final String ERROR_MESSAGE_IDENTIFICATION_REQUIRED = "Número de identificación es requerido";
    public static final String ERROR_MESSAGE_SERVICE_UNAVAILABLE = "Servicio no disponible temporalmente";
    public static final String ERROR_MESSAGE_INTERNAL_ERROR = "Error interno del servidor";
    public static final String ERROR_MESSAGE_UNAUTHORIZED = "Token expirado o inválido";
    public static final String ERROR_MESSAGE_BAD_REQUEST = "Solicitud incorrecta";
    public static final String ERROR_MESSAGE_TIMEOUT = "Timeout al conectar con servicios backend";
    
    // Response Messages (Spanish for responses)
    public static final String RESPONSE_MESSAGE_SUCCESS = "Consulta procesada exitosamente";
    public static final String RESPONSE_MESSAGE_ERROR = "Error en el procesamiento";
    public static final String RESPONSE_MESSAGE_VALIDATION_ERROR = "Error de validación en los datos de entrada";
    
    // Validation Messages (Spanish for responses)
    public static final String VALIDATION_MESSAGE_IDENTIFICATION_REQUIRED = "Identificación es requerida";
    public static final String VALIDATION_MESSAGE_IDENTIFICATION_TYPE_REQUIRED = "Tipo de identificación es requerido";
    public static final String VALIDATION_MESSAGE_INVALID_IDENTIFICATION_TYPE = "Tipo de identificación debe ser: Cedula o RNC";
    public static final String VALIDATION_MESSAGE_INVALID_FORCE_UPDATE = "forzarActualizar debe ser TRUE o FALSE";
    public static final String VALIDATION_MESSAGE_INVALID_INCLUDE_BINARY_PHOTO = "incluirFotoBinaria debe ser TRUE o FALSE";
    public static final String VALIDATION_MESSAGE_CEDULA_FORMAT = "Número de cédula debe contener solo dígitos numéricos";
    public static final String VALIDATION_MESSAGE_CEDULA_LENGTH = "Número de cédula debe tener exactamente 11 dígitos";
    public static final String VALIDATION_MESSAGE_RNC_FORMAT = "Número de RNC debe contener solo dígitos numéricos";
    public static final String VALIDATION_MESSAGE_RNC_LENGTH = "Número de RNC debe tener entre 9 y 11 dígitos";
    
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
    public static final String HEADER_SOAP_ACTION = "SOAPAction";
    
    // Default Values
    public static final String DEFAULT_FORCE_UPDATE = BOOLEAN_FALSE;
    public static final String DEFAULT_INCLUDE_BINARY_PHOTO = BOOLEAN_FALSE;
    public static final String DEFAULT_CANAL = "MICM";
    public static final String DEFAULT_VERSION = "1";
    public static final String DEFAULT_SERVICIO = "ConsultarDatosGeneralesCliente";
    public static final String DEFAULT_DATE_TIME = "0001-01-01T00:00:00";
    public static final String DEFAULT_DATE = "0001-01-01";
    
    // Service Names for Logging (English for internal use)
    public static final String SERVICE_LEGAL_CLIENT_GENERAL_DATA = "LegalClientGeneralDataService";
    public static final String SERVICE_MASTER_DATA_QUERY = "MasterDataQueryService";
    public static final String SERVICE_JCE_DATA_QUERY = "JCEDataQueryService";
    public static final String SERVICE_UPDATE_MASTER_DATA = "UpdateMasterDataService";
    
    // Orchestration Property Names (English for internal use)
    public static final String PROPERTY_CALL_LEGAL_CLIENT_SERVICE = "callLegalClientService";
    public static final String PROPERTY_CALL_MASTER_DATA_SERVICE = "callMasterDataService";
    public static final String PROPERTY_CALL_JCE_SERVICE = "callJceService";
    public static final String PROPERTY_CALL_UPDATE_MASTER_SERVICE = "callUpdateMasterService";
    public static final String PROPERTY_CLIENT_NOT_FOUND_IN_MASTER = "clientNotFoundInMaster";
    public static final String PROPERTY_HAS_BACKEND_ERROR = "hasBackendError";
    
    // Exchange Property Names (English for internal use)
    public static final String PROPERTY_IDENTIFICATION_NUMBER_RQ = "identificationNumberRq";
    public static final String PROPERTY_IDENTIFICATION_TYPE_RQ = "identificationTypeRq";
    public static final String PROPERTY_FORCE_UPDATE_RQ = "forceUpdateRq";
    public static final String PROPERTY_INCLUDE_BINARY_PHOTO_RQ = "includeBinaryPhotoRq";
    public static final String PROPERTY_ORIGINAL_SESSION_ID = "originalSessionId";
    public static final String PROPERTY_REQUEST_AUDIT = "requestAudit";
    public static final String PROPERTY_BACKEND_ERROR_MESSAGE = "backendErrorMessage";
    public static final String PROPERTY_BACKEND_ERROR_CODE = "backendErrorCode";
    
    // Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_SOAP = "text/xml; charset=utf-8";
    
    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no debe ser instanciada");
    }
}