package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping({"/api/prescriptions", "/api/prescription"})
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final AuthService authService;

    public PrescriptionController(PrescriptionService prescriptionService, AuthService authService) {
        this.prescriptionService = prescriptionService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Prescription> createPrescription(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody Prescription prescription) {
        authService.requireRole(token, "DOCTOR");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.savePrescription(prescription));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Prescription>> getByAppointmentId(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long appointmentId) {
        authService.requireAnyRole(token, "DOCTOR", "PATIENT");
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByAppointmentId(appointmentId));
    }

    @GetMapping("/patient/{patientName}")
    public ResponseEntity<List<Prescription>> getByPatientName(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable String patientName) {
        authService.requireAnyRole(token, "DOCTOR", "PATIENT");
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientName(patientName));
    }
}
