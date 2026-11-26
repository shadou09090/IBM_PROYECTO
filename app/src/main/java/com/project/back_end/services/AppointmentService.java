package com.project.back_end.services;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.exceptions.ConflictException;
import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AppointmentDTO bookAppointment(Long doctorId, Long patientId, LocalDateTime appointmentTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        validateNoConflict(doctorId, appointmentTime, null);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(Appointment.STATUS_SCHEDULED);

        return AppointmentDTO.fromEntity(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO updateAppointment(
            Long appointmentId, Long doctorId, LocalDateTime newTime, Integer status) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId));

        if (doctorId != null && !doctorId.equals(existing.getDoctor().getId())) {
            Doctor newDoctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
            existing.setDoctor(newDoctor);
        }

        if (newTime != null) {
            validateNoConflict(existing.getDoctor().getId(), newTime, existing.getId());
            existing.setAppointmentTime(newTime);
        }

        if (status != null) {
            existing.setStatus(status);
        }

        return AppointmentDTO.fromEntity(appointmentRepository.save(existing));
    }

    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment existing = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId));
        existing.setStatus(Appointment.STATUS_CANCELLED);
        appointmentRepository.save(existing);
    }

    public AppointmentDTO getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId));
        return AppointmentDTO.fromEntity(appointment);
    }

    public List<AppointmentDTO> getAppointmentsForDoctor(
            Long doctorId, LocalDate date, String patientName) {
        List<Appointment> appointments;
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            if (patientName != null && !patientName.isBlank()) {
                appointments = appointmentRepository.findByDoctorIdAndDateRangeAndPatientName(
                        doctorId, start, end, patientName.trim());
            } else {
                appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end);
            }
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
            if (patientName != null && !patientName.isBlank()) {
                String query = patientName.trim().toLowerCase();
                appointments = appointments.stream()
                        .filter(a -> a.getPatient().getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }
        }
        return appointments.stream()
                .map(AppointmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(AppointmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validateNoConflict(Long doctorId, LocalDateTime requestedStart, Long excludeAppointmentId) {
        LocalDateTime requestedEnd = requestedStart.plusHours(1);
        LocalDateTime dayStart = requestedStart.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = requestedStart.toLocalDate().atTime(LocalTime.MAX);

        List<Appointment> sameDayAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, dayStart, dayEnd);

        for (Appointment existing : sameDayAppointments) {
            if (excludeAppointmentId != null && excludeAppointmentId.equals(existing.getId())) {
                continue;
            }
            if (existing.getStatus() == Appointment.STATUS_CANCELLED) {
                continue;
            }
            LocalDateTime existingStart = existing.getAppointmentTime();
            LocalDateTime existingEnd = existing.getEndTime();
            boolean overlaps = requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);
            if (overlaps) {
                throw new ConflictException(
                        "Doctor already has an appointment overlapping with " + requestedStart);
            }
        }
    }
}
