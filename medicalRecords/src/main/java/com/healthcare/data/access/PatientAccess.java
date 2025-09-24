package com.healthcare.data.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthcare.data.store.DataSource;
import com.healthcare.data.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Double.parseDouble;

public class PatientAccess {

    public static final String TENANT_ID = "tenant_id";
    public static final String PATIENT_ID = "patient_id";
    public static final String NAME = "name";
    public static final String TOTAL_VISITS = "total_visits";
    public static final String LAST_YEAR_VISITS = "last_year_visits";
    public static final String DIAGNOSIS = "diagnosis";
    public static final String PRIMARY_DOCTOR = "primary_doctor";
    public static final String RISK_SCORE = "risk_score";
    public static final String PATIENT_SUMMARY = "patient_summary";

    public List<Patient> filterPatients(String tenantId, Map<String, String> filters) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + TENANT_ID + ", " + PATIENT_ID + ", " + NAME + ", "
                + TOTAL_VISITS + ", " + LAST_YEAR_VISITS + ", " + DIAGNOSIS + ", " + PRIMARY_DOCTOR + ", "
                + RISK_SCORE + " FROM " + PATIENT_SUMMARY + " WHERE " + TENANT_ID + " = ?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case RISK_SCORE -> {
                    double val = parseDouble(value);
                    if (value.startsWith(">=")) {
                        sql.append(" AND ").append(key).append(" >= ?");
                        params.add(val);
                    } else if (value.startsWith("<=")) {
                        sql.append(" AND ").append(key).append(" <= ?");
                        params.add(val);
                    } else {
                        sql.append(" AND ").append(key).append(" = ?");
                        params.add(val);
                    }
                }
                case TOTAL_VISITS, LAST_YEAR_VISITS -> {
                    if (value.startsWith(">=")) {
                        sql.append(" AND ").append(key).append(" >= ?");
                        params.add(Integer.parseInt(value.substring(2)));
                    } else if (value.startsWith("<=")) {
                        sql.append(" AND ").append(key).append(" <= ?");
                        params.add(Integer.parseInt(value.substring(2)));
                    } else {
                        sql.append(" AND ").append(key).append(" = ?");
                        params.add(Integer.parseInt(value));
                    }
                }
                case NAME, DIAGNOSIS, PRIMARY_DOCTOR -> {
                    sql.append(" AND ").append(key).append(" = ?");
                    params.add(value);
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        }

        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DataSource.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));  // JDBC is 1-based
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getString(PATIENT_ID),
                            rs.getString(TENANT_ID),
                            rs.getString(NAME),
                            rs.getInt(TOTAL_VISITS),
                            rs.getInt(LAST_YEAR_VISITS),
                            rs.getString(PRIMARY_DOCTOR),
                            rs.getDouble(RISK_SCORE),
                            rs.getString(DIAGNOSIS)
                    ));
                }
            }
        }
        return patients;
    }

    public List<Patient> findByFilter(String tenantId, JsonNode filter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + TENANT_ID + ", " + PATIENT_ID + ", " + NAME + ", "
                + TOTAL_VISITS + ", " + LAST_YEAR_VISITS + ", " + DIAGNOSIS + ", " + PRIMARY_DOCTOR + ", "
                + RISK_SCORE + " FROM " + PATIENT_SUMMARY + " WHERE " + TENANT_ID + " = ?");

        new StringBuilder("SELECT " + TENANT_ID + ", " + PATIENT_ID + ", " + NAME + ", "
                + TOTAL_VISITS + ", " + LAST_YEAR_VISITS + ", " + DIAGNOSIS + ", " + PRIMARY_DOCTOR + ", "
                + RISK_SCORE + " FROM " + PATIENT_SUMMARY + " WHERE " + TENANT_ID + " = ?");
        List<Object> params = new ArrayList<>();
        params.add(tenantId);

        // Build WHERE clauses based on filter
        Iterator<Map.Entry<String, JsonNode>> fields = filter.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            String valueStr = entry.getValue().asText();

            switch (key) {
                case TOTAL_VISITS, LAST_YEAR_VISITS -> {
                    int val;
                    if (valueStr.startsWith(">=")) {
                        sql.append(" AND ").append(key).append(" >= ?");
                        val = Integer.parseInt(valueStr.substring(2));
                    } else if (valueStr.startsWith("<=")) {
                        sql.append(" AND ").append(key).append(" <= ?");
                        val = Integer.parseInt(valueStr.substring(2));
                    } else {
                        sql.append(" AND ").append(key).append(" = ?");
                        val = Integer.parseInt(valueStr);
                    }
                    params.add(val);
                }
                case RISK_SCORE -> {
                    double val;
                    if (valueStr.startsWith(">=")) {
                        sql.append(" AND risk_score >= ?");
                        val = Double.parseDouble(valueStr.substring(2));
                    } else if (valueStr.startsWith("<=")) {
                        sql.append(" AND risk_score <= ?");
                        val = Double.parseDouble(valueStr.substring(2));
                    } else {
                        sql.append(" AND risk_score = ?");
                        val = Double.parseDouble(valueStr);
                    }
                    params.add(val);
                }
                case NAME , DIAGNOSIS , PRIMARY_DOCTOR -> {
                    sql.append(" AND ").append(key).append(" = ?");
                    params.add(valueStr);
                }
                default -> {
                    // Ignore unknown fields
                }
            }
        }

        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DataSource.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));  // JDBC is 1-based
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getString(PATIENT_ID),
                            rs.getString(TENANT_ID),
                            rs.getString(NAME),
                            rs.getInt(TOTAL_VISITS),
                            rs.getInt(LAST_YEAR_VISITS),
                            rs.getString(PRIMARY_DOCTOR),
                            rs.getDouble(RISK_SCORE),
                            rs.getString(DIAGNOSIS)
                    ));
                }
            }
        }

        return patients;
    }

    public void createPatient(Patient patient) throws SQLException {
        String sql = """
                    INSERT INTO patient_summary
                        (tenant_id, patient_id, name, total_visits, last_year_visits, diagnosis, primary_doctor, risk_score)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DataSource.getDataSource().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getTenantId());
            ps.setString(2, patient.getPatientId());
            ps.setString(3, patient.getPatientName());
            ps.setInt(4, patient.getVisitsTotal());
            ps.setInt(5, patient.getVisitsLast365d());
            ps.setString(6, patient.getDiagnosis());
            ps.setString(7, patient.getPrimaryDoctor());
            ps.setDouble(8, patient.getRiskScore());
            ps.executeUpdate();
        }
    }
}