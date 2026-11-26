package com.project.back_end.services;

import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PrescriptionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Prescription savePrescription(Prescription prescription) {
        Appointment appointment = appointmentRepository.findById(prescription.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + prescription.getAppointmentId()));
        if (prescription.getPatientName() == null || prescription.getPatientName().isBlank()) {
            prescription.setPatientName(appointment.getPatient().getName());
        }
        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getPrescriptionsByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getPrescriptionsByPatientName(String patientName) {
        return prescriptionRepository.findByPatientNameIgnoreCase(patientName);
    }
}
