package com.project.back_end.dto;

import jakarta.validation.constraints.NotBlank;

public class Login {

    @NotBlank(message = "Identifier (email or username) is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;

    public Login() {
    }

    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
