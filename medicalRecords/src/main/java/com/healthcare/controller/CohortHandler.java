package com.healthcare.controller;

import com.healthcare.data.access.CohortAccess;
import com.healthcare.data.access.PatientAccess;
import com.healthcare.data.model.Cohort;
import com.healthcare.data.model.Patient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CohortHandler implements HttpHandler {
    private final CohortAccess cohortDao;
    private final PatientAccess patientDao;
    private final ObjectMapper mapper = new ObjectMapper();

    public CohortHandler(CohortAccess cohortDao, PatientAccess patientDao) {
        this.cohortDao = cohortDao;
        this.patientDao = patientDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.endsWith("/query")) {
            handleQueryCohort(exchange);
        } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleListCohorts(exchange);
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod()) && path.endsWith("/create")) {
            handleCreateCohort(exchange);
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void handleListCohorts(HttpExchange exchange) throws IOException {
        String tenantId = HttpMedicalRecordsApplication.getQueryParam(exchange, "tenantId");
        if (tenantId == null) {
            HttpMedicalRecordsApplication.sendResponse(exchange, 400, "tenantId required");
            return;
        }
        try {
            List<Cohort> cohorts = cohortDao.listCohorts(tenantId);
            String response = mapper.writeValueAsString(cohorts);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (SQLException e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleCreateCohort(HttpExchange exchange) throws IOException {
        try {
            Cohort cohort = mapper.readValue(exchange.getRequestBody(), Cohort.class);
            cohortDao.createCohort(cohort);
            String response = mapper.writeValueAsString(cohort);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sendError(exchange, e.getMessage());
        }
    }

    private void handleQueryCohort(HttpExchange exchange) throws IOException {
        String tenantId = HttpMedicalRecordsApplication.getQueryParam(exchange, "tenantId");
        if (tenantId == null) {
            HttpMedicalRecordsApplication.sendResponse(exchange, 400, "tenantId required");
            return;
        }
        String cohortName = null;
        String query = exchange.getRequestURI().getQuery();

        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2) {
                    if ("tenantId".equals(kv[0])) tenantId = kv[1];
                    if ("cohortName".equals(kv[0])) cohortName = kv[1];
                }
            }
        }

        if (cohortName == null) {
            sendError(exchange, "Missing cohortName parameter");
            return;
        }

        try {
            Optional<Cohort> cohortOpt = cohortDao.findByName(tenantId, cohortName);
            if (cohortOpt.isEmpty()) {
                sendError(exchange, "Cohort not found");
                return;
            }

            Cohort cohort = cohortOpt.get();
            JsonNode filter = cohort.getFilterSpec();

            List<Patient> patients = patientDao.findByFilter(tenantId, filter);

            String response = mapper.writeValueAsString(patients) + "\nTotal: " + patients.size();;
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void sendError(HttpExchange exchange, String message) throws IOException {
        String err = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(500, err.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(err.getBytes());
        }
    }
}
