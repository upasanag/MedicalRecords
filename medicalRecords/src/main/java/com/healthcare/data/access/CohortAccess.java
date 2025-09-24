package com.healthcare.data.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.data.store.DataSource;
import com.healthcare.data.model.Cohort;

import java.sql.*;
import java.util.*;

public class CohortAccess {

    public void createCohort(final Cohort cohort) throws SQLException {
        String sql = "INSERT INTO cohorts (cohort_id, cohort_name, tenant_id, filter_spec) " +
                "VALUES (?, ?, ?, ?::jsonb)";
        try (final Connection conn = DataSource.getDataSource().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cohort.getCohortId());
            ps.setString(2, cohort.getName());
            ps.setString(3, cohort.getTenantId());
            ps.setString(4, String.valueOf(cohort.getFilterSpec()));
            ps.executeUpdate();
        }
    }

    /**
     * List all cohorts for a tenant
     */
    public List<Cohort> listCohorts(String tenantId) throws SQLException, JsonProcessingException {
        String sql = "SELECT cohort_id, cohort_name, tenant_id, filter_spec FROM cohorts WHERE tenant_id = ?";
        List<Cohort> cohorts = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (Connection conn = DataSource.getDataSource().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cohorts.add(new Cohort(
                            rs.getString("cohort_id"),
                            rs.getString("cohort_name"),
                            rs.getString("tenant_id"),
                            mapper.readTree(rs.getString("filter_spec"))
                    ));
                }
            }
        }
        return cohorts;
    }

    /**
     * Get cohort by cohortName
     */
    public Optional<Cohort> findByName(String tenantId, String cohortName) throws SQLException, JsonProcessingException {
        String sql = "SELECT cohort_id, cohort_name, tenant_id, filter_spec FROM cohorts WHERE tenant_id = ? AND cohort_name = ?";
        ObjectMapper mapper = new ObjectMapper();
        try (Connection conn = DataSource.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenantId);
            ps.setString(2, cohortName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Cohort(
                            rs.getString("cohort_id"),
                            rs.getString("tenant_id"),
                            rs.getString("cohort_name"),
                            mapper.readTree(rs.getString("filter_spec"))
                    ));
                }
            }
        }
        return Optional.empty();
    }
}