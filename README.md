# MedicalRecords
 A Core Java REST APIs that allows medical institutions to query, filter, and analyze patient data and manage reusable patient cohorts.

 Built with:
 
	1) Language: Core Java
	2) JSON serialization and deserialization: Jackson 
	3) Database: PostgreSQL
	4) Build tool: Maven

 Features:
  
	1) Patients: create, list, filter with comparison operators.
	2) Manage cohorts: create, list, filter patients as per the filter description in cohorts

 Prerequisites:
  
	1) Java
	2) Maven
	3) Docker (to run PostgreSQL)

 **Setup**:

    1) Clone & Build:
          git clone git@github.com:upasanag/MedicalRecords.git
          cd MedicalRecords
          mvn clean package
    2) Run PostgreSQL with Docker:
          docker run --name healthdash-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=healthcare -p 5432:5432 -d postgres:15
    3) Create Database Schema:
          Schema is present in the repository, in the file resources/sql/schema.sql
    4) Run the Server:
          java -cp target/medicalRecords-corejava-0.0.1.jar com.healthcare.controller.HttpMedicalRecordsApplication

**APIs**:

	1) Create Patient:
                    curl -X POST "http://localhost:8080/api/v1/patients" \
                    -H "Content-Type: application/json" \
                    -d '{
                    	"tenantId":"5",
                      "patientId":"11",
                      "patientName":"Kim Smith",
                      "visitsTotal":10,
                      "visitsLast365d":3,
                      "diagnosis":"Migraine",
                      "primaryDoctor":"Dr. Alex",
                      "riskScore":0.55
                    }'
  	
    2) List Patients:
                    curl "http://localhost:8080/api/v1/patients?tenantId=5&primary_doctor=Kim%20Smith&risk_score=0.55"
    
    3) Create Cohort:
                  curl -X POST "http://localhost:8080/api/v1/cohorts/create" \
                  -H "Content-Type: application/json" \
                  -d '{
                    "cohortId": "1",
                    "name": "Migraine Patients",
                    "tenantId": "5",
                    "filterSpec": {
                      "diagnosis": "Migraine",
                      "num_visits": ">=5",
                      "risk_score": ">=0.4"
                    }
                  }'

    4) List Patients:
                    curl "http://localhost:8080/api/v1/cohorts?tenantId=5"
                    curl "http://localhost:8080/api/v1/cohorts?tenantId=5&cohortName=Migraine%20Patients"
                    
    5) Filter patients as per the filter description in a cohort:
                    curl "http://localhost:8080/api/v1/cohorts/query?tenantId=5&cohortName=Migraine%20Patients"
