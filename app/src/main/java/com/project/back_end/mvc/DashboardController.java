package com.project.back_end.mvc;

import com.project.back_end.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    private final AuthService authService;

    public DashboardController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        if (!authService.validateToken(token, "ADMIN")) {
            return "redirect:/";
        }
        return "admin/adminDashboard";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        if (!authService.validateToken(token, "DOCTOR")) {
            return "redirect:/";
        }
        return "doctor/doctorDashboard";
    }
}
