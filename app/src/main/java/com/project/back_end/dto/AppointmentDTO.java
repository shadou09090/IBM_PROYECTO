package com.project.back_end.dto;

import com.project.back_end.models.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;
    private LocalDateTime appointmentTime;
    private LocalDateTime endTime;
    private LocalDate appointmentDate;
    private LocalTime appointmentTimeOnly;
    private int status;
    private String statusLabel;

    public AppointmentDTO() {
    }

    public static AppointmentDTO fromEntity(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());
            dto.setDoctorName(appointment.getDoctor().getName());
            dto.setDoctorSpecialty(appointment.getDoctor().getSpecialty());
        }
        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatientName(appointment.getPatient().getName());
            dto.setPatientEmail(appointment.getPatient().getEmail());
            dto.setPatientPhone(appointment.getPatient().getPhone());
            dto.setPatientAddress(appointment.getPatient().getAddress());
        }
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setEndTime(appointment.getEndTime());
        if (appointment.getAppointmentTime() != null) {
            dto.setAppointmentDate(appointment.getAppointmentTime().toLocalDate());
            dto.setAppointmentTimeOnly(appointment.getAppointmentTime().toLocalTime());
        }
        dto.setStatus(appointment.getStatus());
        String statusText;
        switch (appointment.getStatus()) {
            case Appointment.STATUS_SCHEDULED:
                statusText = "Scheduled";
                break;
            case Appointment.STATUS_COMPLETED:
                statusText = "Completed";
                break;
            case Appointment.STATUS_CANCELLED:
                statusText = "Cancelled";
                break;
            default:
                statusText = "Unknown";
                break;
        }
        dto.setStatusLabel(statusText);
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public void setAppointmentTimeOnly(LocalTime appointmentTimeOnly) {
        this.appointmentTimeOnly = appointmentTimeOnly;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }
}
