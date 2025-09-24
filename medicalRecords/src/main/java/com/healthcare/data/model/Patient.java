package com.healthcare.data.model;

public class Patient {
    private  String patientId;
    private  String tenantId;
    private String patientName;
    private int visitsTotal;
    private int visitsLast365d;
    private String primaryDoctor;
    private double riskScore;
    private String diagnosis;

    public Patient() {
    }

    public Patient(String patientId, String tenantId, String patientName, int visitsTotal, int visitsLast365d,
                   String primaryDoctor, double riskScore, String diagnosis) {
        this.patientId = patientId;
        this.tenantId = tenantId;
        this.visitsTotal = visitsTotal;
        this.visitsLast365d = visitsLast365d;
        this.primaryDoctor = primaryDoctor;
        this.riskScore = riskScore;
        this.diagnosis = diagnosis;
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getVisitsTotal() {
        return visitsTotal;
    }

    public int getVisitsLast365d() {
        return visitsLast365d;
    }

    public String getPrimaryDoctor() {
        return primaryDoctor;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", visitsTotal=" + visitsTotal +
                ", visitsLast365d=" + visitsLast365d +
                ", primaryDoctor='" + primaryDoctor + '\'' +
                ", riskScore=" + riskScore +
                ", diagnosis='" + diagnosis + '\'' +
                '}';
    }
}