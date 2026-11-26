package com.project.back_end.services;

import com.project.back_end.dto.Login;
import com.project.back_end.exceptions.ConflictException;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.exceptions.UnauthorizedException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Transactional
    public Doctor saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new ConflictException("A doctor with email '" + doctor.getEmail() + "' already exists");
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, Doctor updated) {
        Doctor existing = getDoctorById(id);
        existing.setName(updated.getName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setPhone(updated.getPhone());
        if (updated.getAvailableTimes() != null) {
            existing.setAvailableTimes(updated.getAvailableTimes());
        }
        return doctorRepository.save(existing);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }

    public Map<String, Object> validateDoctorLogin(Login login) {
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid doctor email or password"));

        if (!doctor.getPassword().equals(login.getPassword())) {
            throw new UnauthorizedException("Invalid doctor email or password");
        }

        String token = tokenService.generateToken(doctor.getEmail(), "DOCTOR", doctor.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", "DOCTOR");
        response.put("userId", doctor.getId());
        response.put("name", doctor.getName());
        response.put("specialty", doctor.getSpecialty());
        return response;
    }

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = getDoctorById(doctorId);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        Set<String> bookedStarts = booked.stream()
                .filter(a -> a.getStatus() != Appointment.STATUS_CANCELLED)
                .map(a -> a.getAppointmentTime().toLocalTime().format(formatter).toUpperCase(Locale.ENGLISH))
                .collect(Collectors.toSet());

        return doctor.getAvailableTimes().stream()
                .filter(slot -> {
                    String startPart = slot.split("-")[0].trim().toUpperCase(Locale.ENGLISH);
                    return !bookedStarts.contains(startPart);
                })
                .collect(Collectors.toList());
    }

    public List<Doctor> filterDoctors(String name, String timePeriod, String specialty) {
        List<Doctor> doctors = doctorRepository.findAll();

        return doctors.stream()
                .filter(d -> name == null || name.isBlank()
                        || d.getName().toLowerCase(Locale.ENGLISH)
                                .contains(name.trim().toLowerCase(Locale.ENGLISH)))
                .filter(d -> specialty == null || specialty.isBlank()
                        || d.getSpecialty().equalsIgnoreCase(specialty.trim()))
                .filter(d -> timePeriod == null || timePeriod.isBlank()
                        || d.getAvailableTimes().stream()
                                .anyMatch(t -> t.toUpperCase(Locale.ENGLISH)
                                        .contains(timePeriod.trim().toUpperCase(Locale.ENGLISH))))
                .collect(Collectors.toList());
    }
}
