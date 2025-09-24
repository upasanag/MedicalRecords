package com.healthcare.data.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Cohort {
    private String cohortId;
    private String tenantId;
    private String name;
    private JsonNode FilterSpec;

    public void setCohortId(String cohortId) {
        this.cohortId = cohortId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilterSpec(JsonNode filterSpec) {
        this.FilterSpec = filterSpec;
    }

    public Cohort() {
    }

    public Cohort(String cohortId, String tenantId, String name, JsonNode FilterSpec) {
        this.cohortId = cohortId; this.tenantId = tenantId; this.name = name; this.FilterSpec = FilterSpec;
    }
    public String getCohortId() { return cohortId; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public JsonNode getFilterSpec() { return FilterSpec; }
}