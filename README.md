# SmartCare Clinic — Hospital CMS

A full-stack **Smart Clinic Management System (Hospital CMS)** built with **Spring Boot 3 (Java 17)**, **MySQL**, **MongoDB**, **Thymeleaf**, and modular **ES6 JavaScript**.

## Features
- **Role-Based Portals**: Dedicated workflows for **Admin**, **Doctor**, and **Patient** backed by HMAC-SHA256 JWT authentication.
- **Polyglot Persistence**:
  - **MySQL (`cms`)**: Admins, Doctors, Patients, and Appointments via Spring Data JPA.
  - **MongoDB (`cms`)**: Clinical Prescriptions via Spring Data MongoDB.
- **Conflict-Free Scheduling**: 1-hour appointment slots with overlap validation and per-date doctor availability checks.
- **Containerized & CI-Ready**: Multi-stage Docker build and GitHub Actions pipelines for backend, frontend, and Dockerfile linting.

---

## Prerequisites
- **Java 17** (Eclipse Temurin recommended)
- **Maven 3.9+**
- **MySQL 8.0** (running on `localhost:3306`, schema `cms` auto-created)
- **MongoDB 7.0** (running on `localhost:27017`, database `cms`)
- **Docker** (optional, for containerized execution)

---

## Local Setup & Run

1. **Configure Environment Variables** (optional overrides for [application.properties](app/src/main/resources/application.properties)):
   ```bash
   export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/cms?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   export SPRING_DATASOURCE_USERNAME="root"
   export SPRING_DATASOURCE_PASSWORD="root"
   export SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/cms"
   export JWT_SECRET="YourSuperSecretKeyForJwtSigning2026!"
   ```

2. **Load Sample Data & Stored Procedures** (optional):
   Run the SQL scripts in [sql/](sql/) against your MySQL `cms` database.

3. **Verify & Build the Backend**:
   ```bash
   cd app
   mvn checkstyle:check
   mvn clean compile
   ```

4. **Run the Application**:
   ```bash
   cd app
   mvn spring-boot:run
   ```
   Open `http://localhost:8080/` in your browser.

---

## Docker Build & Run

```bash
docker build -t smartcare-cms:latest .
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/cms?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e SPRING_DATA_MONGODB_URI="mongodb://host.docker.internal:27017/cms" \
  smartcare-cms:latest
```

---

## Key Deliverables

| Component | Path |
| :--- | :--- |
| Doctor REST Controller | [DoctorController.java](app/src/main/java/com/project/back_end/controllers/DoctorController.java) |
| Prescription REST Controller | [PrescriptionController.java](app/src/main/java/com/project/back_end/controllers/PrescriptionController.java) |
| Patient Repository | [PatientRepository.java](app/src/main/java/com/project/back_end/repo/PatientRepository.java) |
| Appointment Service | [AppointmentService.java](app/src/main/java/com/project/back_end/services/AppointmentService.java) |
| Token Service (JWT) | [TokenService.java](app/src/main/java/com/project/back_end/services/TokenService.java) |
| Doctor Service | [DoctorService.java](app/src/main/java/com/project/back_end/services/DoctorService.java) |
| MVC Dashboard Controller | [DashboardController.java](app/src/main/java/com/project/back_end/mvc/DashboardController.java) |
| Application Properties | [application.properties](app/src/main/resources/application.properties) |
| Multi-Stage Dockerfile | [Dockerfile](Dockerfile) |
| Backend CI Workflow | [backend-ci.yml](.github/workflows/backend-ci.yml) |

---

## Documentation
- [Architecture Overview](docs/ARCHITECTURE.md)
- [User Stories & Role Matrix](docs/USER_STORIES.md)
