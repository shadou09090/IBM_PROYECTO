package com.project.back_end.services;

import com.project.back_end.dto.Login;
import com.project.back_end.exceptions.ConflictException;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository, TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public Patient createPatient(Patient patient) {
        if (patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isPresent()) {
            throw new ConflictException("Patient already exists with that email or phone number");
        }
        return patientRepository.save(patient);
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
    }

    public Map<String, Object> validatePatientLogin(Login login) {
        Patient patient = patientRepository.findByEmail(login.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid patient email or password"));

        if (!patient.getPassword().equals(login.getPassword())) {
            throw new UnauthorizedException("Invalid patient email or password");
        }

        String token = tokenService.generateToken(patient.getEmail(), "PATIENT", patient.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "PATIENT");
        response.put("userId", patient.getId());
        response.put("name", patient.getName());
        response.put("email", patient.getEmail());
        return response;
    }
}
