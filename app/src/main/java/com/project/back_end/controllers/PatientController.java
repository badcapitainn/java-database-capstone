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

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

/**
 * PatientController
 * 
 * REST controller for handling operations related to the Patient entity.
 * Allows patient registration, login, fetching patient details, getting and filtering patient appointments.
 */
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    // Constructor injection for dependencies
    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * Handles HTTP GET requests to retrieve patient details using a token.
     * 
     * @param token The authentication token for the patient
     * @return ResponseEntity containing patient details or an error message
     */
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // If token is valid, fetch the patient details using patientService.getPatientDetails()
            return patientService.getPatientDetails(token);

        } catch (Exception e) {
            response.put("error", "Error retrieving patient details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP POST requests for patient registration.
     * 
     * @param patient The patient details to be created
     * @return ResponseEntity with success or error message
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate if the patient already exists by checking email or phone number
            boolean isValid = service.validatePatient(patient);

            if (!isValid) {
                // Conflict - Patient already exists
                response.put("message", "Patient with email id or phone no already exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // If validation passes, call patientService.createPatient() to create a new patient record
            int createResult = patientService.createPatient(patient);

            if (createResult == 1) {
                // Success
                response.put("message", "Signup successful");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // Internal error
                response.put("message", "Internal server error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP POST requests for patient login.
     * 
     * @param login The login credentials (email as identifier, password)
     * @return ResponseEntity with token if valid, or error message if not
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        // Calls service.validatePatientLogin() to validate the patient's login credentials
        return service.validatePatientLogin(login);
    }

    /**
     * Handles HTTP GET requests to fetch appointment details for a specific patient.
     * 
     * @param id The ID of the patient
     * @param token The authentication token for the patient
     * @return ResponseEntity containing the list of patient appointments or an error message
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // If valid, fetch the patient's appointments using patientService.getPatientAppointment()
            return patientService.getPatientAppointment(id, token);

        } catch (Exception e) {
            response.put("error", "Error retrieving appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
     * 
     * @param condition The condition to filter appointments by (e.g., "past", "future")
     * @param name The name or description for filtering (e.g., doctor name)
     * @param token The authentication token for the patient
     * @return ResponseEntity containing the filtered appointments or an error message
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Handle "null" strings as null
            String conditionFilter = ("null".equalsIgnoreCase(condition)) ? null : condition;
            String nameFilter = ("null".equalsIgnoreCase(name)) ? null : name;

            // If valid, call service.filterPatient() to filter the patient's appointments
            return service.filterPatient(conditionFilter, nameFilter, token);

        } catch (Exception e) {
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
