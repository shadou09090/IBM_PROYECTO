package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
@RequestMapping({"/api/doctor", "/api/doctors"})
public class DoctorController {

    private final DoctorService doctorService;
    private final AuthService authService;

    public DoctorController(DoctorService doctorService, AuthService authService) {
        this.doctorService = doctorService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody Login login) {
        return ResponseEntity.ok(doctorService.validateDoctorLogin(login));
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String specialty) {
        authService.requireAnyRole(token, "ADMIN", "DOCTOR", "PATIENT");
        return ResponseEntity.ok(doctorService.filterDoctors(name, time, specialty));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Doctor>> filterDoctors(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String specialty) {
        authService.requireAnyRole(token, "ADMIN", "DOCTOR", "PATIENT");
        return ResponseEntity.ok(doctorService.filterDoctors(name, time, specialty));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireAnyRole(token, "ADMIN", "DOCTOR", "PATIENT");
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<List<String>> getDoctorAvailability(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        authService.requireAnyRole(token, "ADMIN", "DOCTOR", "PATIENT");
        return ResponseEntity.ok(doctorService.getDoctorAvailability(id, date));
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody Doctor doctor) {
        authService.requireRole(token, "ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.saveDoctor(doctor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id,
            @Valid @RequestBody Doctor doctor) {
        authService.requireRole(token, "ADMIN");
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireRole(token, "ADMIN");
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }
}
