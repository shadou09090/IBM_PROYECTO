package com.project.back_end.controllers;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/appointments", "/api/appointment"})
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AuthService authService;
    private final TokenService tokenService;

    public AppointmentController(
            AppointmentService appointmentService,
            AuthService authService,
            TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody BookingRequest request) {
        authService.requireRole(token, "PATIENT");
        Long tokenPatientId = tokenService.extractUserId(token);
        Long patientId = request.getPatientId() != null ? request.getPatientId() : tokenPatientId;
        if (!patientId.equals(tokenPatientId)) {
            throw new UnauthorizedException("Patients can only book appointments for themselves");
        }
        AppointmentDTO created = appointmentService.bookAppointment(
                request.getDoctorId(), patientId, request.getAppointmentTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        authService.requireAnyRole(token, "PATIENT", "DOCTOR");
        AppointmentDTO existing = appointmentService.getAppointmentById(id);
        verifyAppointmentOwnership(token, existing);
        AppointmentDTO updated = appointmentService.updateAppointment(
                id, request.getDoctorId(), request.getAppointmentTime(), request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireRole(token, "PATIENT");
        AppointmentDTO existing = appointmentService.getAppointmentById(id);
        verifyAppointmentOwnership(token, existing);
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireAnyRole(token, "PATIENT", "DOCTOR", "ADMIN");
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        verifyAppointmentOwnership(token, appointment);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAppointments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String patientName) {
        authService.requireAnyRole(token, "DOCTOR", "ADMIN");
        if ("DOCTOR".equalsIgnoreCase(tokenService.extractRole(token))
                && !doctorId.equals(tokenService.extractUserId(token))) {
            throw new UnauthorizedException("Doctors can only view their own appointments");
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsForDoctor(doctorId, date, patientName));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long patientId) {
        authService.requireAnyRole(token, "PATIENT", "DOCTOR", "ADMIN");
        if ("PATIENT".equalsIgnoreCase(tokenService.extractRole(token))
                && !patientId.equals(tokenService.extractUserId(token))) {
            throw new UnauthorizedException("Patients can only view their own appointments");
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId));
    }

    private void verifyAppointmentOwnership(String token, AppointmentDTO appointment) {
        String role = tokenService.extractRole(token);
        Long userId = tokenService.extractUserId(token);
        if ("PATIENT".equalsIgnoreCase(role) && !appointment.getPatientId().equals(userId)) {
            throw new UnauthorizedException("Patients can only access their own appointments");
        }
        if ("DOCTOR".equalsIgnoreCase(role) && !appointment.getDoctorId().equals(userId)) {
            throw new UnauthorizedException("Doctors can only access their own appointments");
        }
    }

    public static class BookingRequest {

        @NotNull(message = "Doctor ID is required")
        private Long doctorId;

        private Long patientId;

        @NotNull(message = "Appointment time is required")
        @Future(message = "Appointment time must be in the future")
        private LocalDateTime appointmentTime;

        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public Long getPatientId() {
            return patientId;
        }

        public void setPatientId(Long patientId) {
            this.patientId = patientId;
        }

        public LocalDateTime getAppointmentTime() {
            return appointmentTime;
        }

        public void setAppointmentTime(LocalDateTime appointmentTime) {
            this.appointmentTime = appointmentTime;
        }
    }

    public static class UpdateAppointmentRequest {

        private Long doctorId;

        @Future(message = "Appointment time must be in the future")
        private LocalDateTime appointmentTime;

        private Integer status;

        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public LocalDateTime getAppointmentTime() {
            return appointmentTime;
        }

        public void setAppointmentTime(LocalDateTime appointmentTime) {
            this.appointmentTime = appointmentTime;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
