package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.back_end.services.Service;

import java.util.Map;

/**
 * Dashboard Controller
 * 
 * This controller handles view rendering for Thymeleaf templates after validating 
 * a token for either admin or doctor users. It serves as a gatekeeper to the Thymeleaf 
 * dashboard views by verifying access tokens for authenticated users.
 */
@Controller
public class DashboardController {

    // Autowire the required service for handling the token validation logic
    private final Service service;

    @Autowired
    public DashboardController(Service service) {
        this.service = service;
    }

    /**
     * Define the adminDashboard method
     * Handles HTTP GET requests to /adminDashboard/{token}
     * 
     * @param token - Admin's authentication token from the path variable
     * @return View name for Thymeleaf template or redirect URL
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        // Call validateToken(token, "admin") using the service
        Map<String, String> validationResult = service.validateToken(token, "admin");
        
        // Check if the returned map is empty
        if (validationResult.isEmpty()) {
            // If empty: Token is valid → return the admin/adminDashboard view
            return "admin/adminDashboard";
        } else {
            // If not empty: Redirect to login page
            return "redirect:/";
        }
    }

    /**
     * Define the doctorDashboard method
     * Handles HTTP GET requests to /doctorDashboard/{token}
     * 
     * @param token - Doctor's authentication token from the path variable
     * @return View name for Thymeleaf template or redirect URL
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        // Call validateToken(token, "doctor") and apply the same logic
        Map<String, String> validationResult = service.validateToken(token, "doctor");
        
        // Check if the returned map is empty
        if (validationResult.isEmpty()) {
            // If empty: Token is valid → return the doctor/doctorDashboard view
            return "doctor/doctorDashboard";
        } else {
            // If not empty: Redirect to login page
            return "redirect:/";
        }
    }
}
