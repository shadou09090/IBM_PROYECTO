package com.project.back_end.controllers;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.dto.Login;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.models.Patient;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/patient", "/api/patients"})
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final AuthService authService;
    private final TokenService tokenService;

    public PatientController(
            PatientService patientService,
            AppointmentService appointmentService,
            AuthService authService,
            TokenService tokenService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping({"/register", ""})
    public ResponseEntity<Patient> register(@Valid @RequestBody Patient patient) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(patient));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody Login login) {
        return ResponseEntity.ok(patientService.validatePatientLogin(login));
    }

    @GetMapping("/me")
    public ResponseEntity<Patient> getCurrentPatient(
            @RequestHeader(value = "Authorization", required = false) String token) {
        authService.requireRole(token, "PATIENT");
        Long userId = tokenService.extractUserId(token);
        return ResponseEntity.ok(patientService.getPatientById(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireAnyRole(token, "PATIENT", "DOCTOR", "ADMIN");
        verifyPatientAccessIfPatientRole(token, id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireAnyRole(token, "PATIENT", "DOCTOR", "ADMIN");
        verifyPatientAccessIfPatientRole(token, id);
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(id));
    }

    private void verifyPatientAccessIfPatientRole(String token, Long requestedPatientId) {
        String role = tokenService.extractRole(token);
        if ("PATIENT".equalsIgnoreCase(role)) {
            Long tokenUserId = tokenService.extractUserId(token);
            if (!requestedPatientId.equals(tokenUserId)) {
                throw new UnauthorizedException("Patients can only access their own records");
            }
        }
    }
}
