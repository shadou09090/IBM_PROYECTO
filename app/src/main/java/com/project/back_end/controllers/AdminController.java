package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.AuthService;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final DoctorService doctorService;

    public AdminController(AuthService authService, DoctorService doctorService) {
        this.authService = authService;
        this.doctorService = doctorService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody Login login) {
        return ResponseEntity.ok(authService.validateAdminLogin(login));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors(
            @RequestHeader(value = "Authorization", required = false) String token) {
        authService.requireRole(token, "ADMIN");
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> addDoctor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Valid @RequestBody Doctor doctor) {
        authService.requireRole(token, "ADMIN");
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.saveDoctor(doctor));
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        authService.requireRole(token, "ADMIN");
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }
}
