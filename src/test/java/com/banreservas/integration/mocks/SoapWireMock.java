package com.banreservas.integration.mocks;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mock de WireMock para simular el servicio backend de Cliente Banreservas.
 * Proporciona respuestas simuladas para diferentes escenarios de prueba.
 * 
 * @author Domingo Junior Ruiz - c-djruiz@banreservas.com
 * @since 20/06/2025
 * @version 1.0.0
 */
@ApplicationScoped
public class SoapWireMock implements QuarkusTestResourceLifecycleManager {

    static final Logger logger = Logger.getLogger(SoapWireMock.class);
    
    private static String responseClienteOk = "";
    private static String responseClienteError = "";

    private static WireMockServer server;

    @Override
    public Map<String, String> start() {

        logger.info("Starting WireMock Server for Cliente Banreservas");

        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();

        try {
            // Intentar leer archivos si existen, sino usar strings vacíos
            try {
                responseClienteOk = new String(
                        Files.readAllBytes(Paths.get("src/test/resources/response/ResponseClienteOk.json")));
            } catch (IOException e) {
                logger.warn("No se pudo leer ResponseClienteOk.json, usando respuesta por defecto");
                responseClienteOk = "{\"header\": {\"responseCode\": 200, \"responseMessage\": \"Success\"}, \"body\": {\"client\": {\"status\": \"Activo\", \"firstName\": \"Juan\", \"lastName\": \"Pérez\"}}}";
            }

            try {
                responseClienteError = new String(
                        Files.readAllBytes(Paths.get("src/test/resources/response/ResponseClienteError.json")));
            } catch (IOException e) {
                logger.warn("No se pudo leer ResponseClienteError.json, usando respuesta por defecto");
                responseClienteError = "{\"header\": {\"responseCode\": 404, \"responseMessage\": \"Cliente no encontrado\"}}";
            }

        } catch (Exception e) {
            logger.error("ERROR! configurando responses: " + e.getMessage());
        }

        // Mock exitoso para cliente encontrado
        server.stubFor(post(urlMatching("/api/v1/cliente-banreservas.*"))
                .withHeader("test", equalTo("success"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseClienteOk)
                        .withStatus(200)));

        // Mock para cliente no encontrado
        server.stubFor(post(urlMatching("/api/v1/cliente-banreservas.*"))
                .withHeader("test", equalTo("not-found"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseClienteError)
                        .withStatus(404)));

        // Mock para error de autenticación
        server.stubFor(post(urlMatching("/api/v1/cliente-banreservas.*"))
                .withHeader("test", equalTo("unauthorized"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"header\": {\"responseCode\": 401, \"responseMessage\": \"Unauthorized\"}}")
                        .withStatus(401)));

        // Mock para error interno del servidor
        server.stubFor(post(urlMatching("/api/v1/cliente-banreservas.*"))
                .withHeader("test", equalTo("server-error"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"header\": {\"responseCode\": 500, \"responseMessage\": \"Internal Server Error\"}}")
                        .withStatus(500)));

        // Mock para servicio no disponible
        server.stubFor(post(urlMatching("/api/v1/cliente-banreservas.*"))
                .withHeader("test", equalTo("service-unavailable"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"header\": {\"responseCode\": 503, \"responseMessage\": \"Service Unavailable\"}}")
                        .withStatus(503)));

        // Mock por defecto para otros casos
        server.stubFor(any(urlMatching(".*"))
                .atPriority(10) 
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("Not found in WireMock")));

        Map<String, String> config = new HashMap<>();
        config.put("backend.banreservas.client.url", server.baseUrl() + "/api/v1/cliente-banreservas");
        config.put("backend.service.timeout", "5000");
        
        logger.info("WireMock started on: " + server.baseUrl());
        logger.info("Backend Cliente URL: " + config.get("backend.banreservas.client.url"));
        
        return config;
    }

    @Override
    public void stop() {
        logger.info("Stopping WireMock Server for Cliente Banreservas");

        if (server != null) {
            server.stop();
        }
        logger.info("WireMock Server Stopped");
    }

    public static void setResponseClienteOk(String response) {
        responseClienteOk = response;
    }

    public static void setResponseClienteError(String response) {
        responseClienteError = response;
    }
}