package com.project.back_end.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull(message = "Patient name is required")
    @NotBlank(message = "Patient name cannot be blank")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    private String patientName;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Medication is required")
    @NotBlank(message = "Medication cannot be blank")
    @Size(min = 2, max = 150, message = "Medication must be between 2 and 150 characters")
    private String medication;

    @NotNull(message = "Dosage is required")
    @NotBlank(message = "Dosage cannot be blank")
    @Size(min = 2, max = 100, message = "Dosage must be between 2 and 100 characters")
    private String dosage;

    @Size(max = 500, message = "Doctor notes cannot exceed 500 characters")
    private String doctorNotes;

    public Prescription() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}
