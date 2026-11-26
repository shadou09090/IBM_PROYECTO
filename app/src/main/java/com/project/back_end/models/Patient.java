package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient name is required")
    @NotBlank(message = "Patient name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;

    @NotNull(message = "Phone number is required")
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number format")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Address is required")
    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String address;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("patient-appointments")
    private List<Appointment> appointments = new ArrayList<>();

    public Patient() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}
