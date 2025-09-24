package com.healthcare.controller;

import com.healthcare.data.access.CohortAccess;
import com.healthcare.data.access.PatientAccess;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class HttpMedicalRecordsApplication {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        PatientAccess patientAccess = new PatientAccess();
        CohortAccess cohortAccess = new CohortAccess();
        server.createContext("/api/v1/patients", new PatientHandler(patientAccess));
        server.createContext("/api/v1/cohorts", new CohortHandler(cohortAccess, patientAccess));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }

    // Parse full query string into Map
    public static Map<String, String> getQueryParams(HttpExchange exchange) {
        Map<String, String> queryParams = new HashMap<>();
        URI requestUri = exchange.getRequestURI();
        String query = requestUri.getRawQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    queryParams.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                    );
                } else {
                    queryParams.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            ""
                    );
                }
            }
        }
        return queryParams;
    }

    // Get a single query parameter by key
    public static String getQueryParam(HttpExchange exchange, String key) {
        Map<String, String> params = getQueryParams(exchange);
        return params.get(key);
    }

    // Send plain text or error response
    public static void sendResponse(HttpExchange exchange, int statusCode, String message) {
        try {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            System.out.println("Exception: "+ e.getMessage());
        }
    }

    // Send JSON response
    public static void sendJson(HttpExchange exchange, int statusCode, String json) {
        try {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            System.out.println("Exception in printing json response: "+ e.getMessage());
        }
    }
}