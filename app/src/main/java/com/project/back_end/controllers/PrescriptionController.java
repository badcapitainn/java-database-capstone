package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;

/**
 * PrescriptionController
 * 
 * REST controller responsible for handling operations related to prescriptions in the system.
 * Allows doctors to save prescriptions and retrieve prescriptions based on the appointment ID.
 */
@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;

    // Constructor injection for dependencies
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService, Service service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    /**
     * Handles HTTP POST requests to save a new prescription for a given appointment.
     * Only doctors can save prescriptions.
     * 
     * @param prescription The prescription details to be saved (passed in the request body)
     * @param token The authentication token for the doctor
     * @return ResponseEntity with success message or error message
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate the token to ensure that the request is made by a doctor
            Map<String, String> validationResult = service.validateToken(token, "doctor");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // If the token is valid, save the prescription using prescriptionService.savePrescription()
            return prescriptionService.savePrescription(prescription);

        } catch (Exception e) {
            response.put("message", "Error saving prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP GET requests to retrieve a prescription by its associated appointment ID.
     * Only doctors can retrieve prescriptions.
     * 
     * @param appointmentId The ID of the appointment to retrieve the prescription for
     * @param token The authentication token for the doctor
     * @return ResponseEntity containing the prescription details or an error message
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() to ensure the request is from a valid doctor
            Map<String, String> validationResult = service.validateToken(token, "doctor");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // If the token is valid, retrieve the prescription using prescriptionService.getPrescription()
            return prescriptionService.getPrescription(appointmentId);

        } catch (Exception e) {
            response.put("error", "Error retrieving prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
