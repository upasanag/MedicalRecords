CREATE TABLE IF NOT EXISTS patient_summary (
    patient_id VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    tenant_id VARCHAR NOT NULL,
    total_visits INT,
    last_year_visits INT,
    primary_doctor VARCHAR,
    diagnosis VARCHAR,
    risk_score DOUBLE
);

CREATE TABLE IF NOT EXISTS cohorts (
    cohort_id VARCHAR PRIMARY KEY,
    tenant_id VARCHAR NOT NULL,
    cohort_name VARCHAR,
    filter_spec VARCHAR
);