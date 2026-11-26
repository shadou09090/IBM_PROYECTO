package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor name is required")
    @NotBlank(message = "Doctor name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Specialty is required")
    @NotBlank(message = "Specialty cannot be blank")
    @Size(min = 2, max = 100, message = "Specialty must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String specialty;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number format")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_available_times", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_times", nullable = false, length = 50)
    private List<String> availableTimes = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("doctor-appointments")
    private List<Appointment> appointments = new ArrayList<>();

    public Doctor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
