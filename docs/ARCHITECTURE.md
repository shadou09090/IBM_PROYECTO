# Architecture Documentation — Smart Clinic Management System (Hospital CMS)

## 1. System Overview
The **Smart Clinic Management System (Hospital CMS)** is a full-stack web application built for SmartCare Solutions. It supports clinic administration, doctor appointment and prescription workflows, and patient self-service booking and medical record viewing.

## 2. Layered Architecture (MVC + REST)
The backend lives in `/app` under the base package `com.project.back_end` and follows a strict layered architecture:

1. **Presentation Layer**
   - **Thymeleaf MVC (`mvc/DashboardController`)**: Serves server-rendered role dashboards (`/adminDashboard/{token}`, `/doctorDashboard/{token}`) after validating the JWT token and role.
   - **REST APIs (`controllers/*`)**: Exposes JSON endpoints under `/api/**` for CRUD operations, search/filtering, authentication, appointment scheduling, and prescriptions.
   - **Static Frontend (`resources/static/`)**: Role selection (`index.html`), patient directory, appointment update, and patient record views powered by modular vanilla ES6 JavaScript (`static/js/`).
2. **Service Layer (`services/*`)**
   - Encapsulates business rules, conflict detection (e.g., overlapping appointments), role-based token validation (`TokenService`, `AuthService`), and transaction boundaries (`@Transactional`).
3. **Persistence Layer (`repo/*` & `models/*`)**
   - **Spring Data JPA** repositories (`AdminRepository`, `DoctorRepository`, `PatientRepository`, `AppointmentRepository`) for relational entities in MySQL.
   - **Spring Data MongoDB** repository (`PrescriptionRepository`) for clinical prescription documents in MongoDB.
4. **Cross-Cutting Concerns (`exceptions/*`)**
   - Centralized `@RestControllerAdvice` (`GlobalExceptionHandler`) mapping validation, authorization, conflict, and not-found exceptions to consistent JSON error payloads.

## 3. Polyglot Persistence Boundary (MySQL vs. MongoDB)
| Store | Entities / Documents | Rationale |
| :--- | :--- | :--- |
| **MySQL (`cms`)** | `admin`, `doctor`, `doctor_available_times`, `patient`, `appointment` | Structured relational data requiring ACID transactions, foreign-key integrity, unique constraints, and analytical SQL stored procedures. |
| **MongoDB (`cms`)** | `prescriptions` (`Prescription`) | Flexible, semi-structured clinical notes, dosage instructions, and medication payloads linked logically by `appointmentId` and `patientName`. |

## 4. JWT Authentication & Authorization Flow
1. A user selects a role (`Admin`, `Doctor`, or `Patient`) on `index.html` and submits credentials to `/api/admin/login`, `/api/doctor/login`, or `/api/patient/login`.
2. The backend verifies credentials and issues a signed HMAC-SHA256 JWT via `TokenService` containing:
   - `sub`: user identifier (`username` for Admin, `email` for Doctor/Patient)
   - `role`: `ADMIN`, `DOCTOR`, or `PATIENT`
   - `userId`: numeric database ID
   - `iat` / `exp`: issued-at and expiration timestamps
3. The frontend stores `token`, `userRole`, and `userId` in `localStorage`.
4. Protected MVC routes (`/adminDashboard/{token}`, `/doctorDashboard/{token}`) validate the token path parameter via `AuthService` and redirect invalid requests to `/`.
5. Protected REST endpoints validate the `Authorization: Bearer <token>` header (or token parameter) against the required role before executing business logic.

## 5. Deployment Topology
- **Containerized App**: Multi-stage `Dockerfile` builds `/app` with `maven:3.9-eclipse-temurin-17` and runs on `eclipse-temurin:17-jre-alpine` as a non-root user on port `8080`.
- **Orchestration (`docker-compose.yml`)**: Connects `cms-app`, `mysql:8.0`, and `mongo:7.0` on a dedicated bridge network with environment-variable overrides for `SPRING_DATASOURCE_URL`, `SPRING_DATA_MONGODB_URI`, and `JWT_SECRET`.
- **CI/CD (`.github/workflows/`)**:
  - `backend-ci.yml`: Java 17, `mvn checkstyle:check`, `mvn clean compile test-compile`.
  - `frontend-ci.yml`: HTMLHint, Stylelint, and ESLint on `resources/templates` and `resources/static`.
  - `docker-ci.yml`: Hadolint validation on `Dockerfile`.
