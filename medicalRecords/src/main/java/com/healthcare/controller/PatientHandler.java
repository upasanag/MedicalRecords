package com.healthcare.controller;

import com.healthcare.data.access.PatientAccess;
import com.healthcare.data.model.Patient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientHandler implements HttpHandler {
    private final PatientAccess dao;
    private final ObjectMapper mapper = new ObjectMapper();

    public PatientHandler(PatientAccess dao) {
        this.dao = dao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            handleListPatients(exchange);
        } else if ("POST".equalsIgnoreCase(method)) {
            handleCreatePatient(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    public void handleListPatients(HttpExchange exchange) {
        String tenantId = HttpMedicalRecordsApplication.getQueryParam(exchange, "tenantId");
        if (tenantId == null) {
            HttpMedicalRecordsApplication.sendResponse(exchange, 400, "tenantId required");
            return;
        }

        Map<String, String> queryParams = HttpMedicalRecordsApplication.getQueryParams(exchange);
        // Remove tenantId (already used separately)
        queryParams.remove("tenantId");

        try {
            // Convert queryParams to generic filter map
            Map<String, String> filterSpec = new HashMap<>(queryParams);
            List<Patient> patients = dao.filterPatients(tenantId, filterSpec);
            ObjectMapper mapper = new ObjectMapper();
            String responseJson = mapper.writeValueAsString(patients) + "\nTotal: " + patients.size();
            HttpMedicalRecordsApplication.sendJson(exchange, 200, responseJson);
        } catch (Exception e) {
            HttpMedicalRecordsApplication.sendResponse(exchange, 500, "Error: " + e.getMessage());
        }
    }


    private void handleCreatePatient(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            Patient patient;
            try {
                patient = mapper.readValue(is, Patient.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
            dao.createPatient(patient);
            String response = mapper.writeValueAsString(patient);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (SQLException e) {
            HttpMedicalRecordsApplication.sendResponse(exchange, 500, "Error: " + e.getMessage());
        }
    }
}
