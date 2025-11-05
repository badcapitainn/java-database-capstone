package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

/**
 * AppointmentController
 * 
 * REST controller for handling all CRUD operations related to appointments.
 * Provides endpoints for booking, retrieving, updating, and canceling appointments.
 * Performs validation on tokens and ensures proper actions are taken based on user roles.
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    // Constructor injection for dependencies
    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * Handles HTTP GET requests to fetch appointments based on date and patient name.
     * Only doctors can access this endpoint.
     * 
     * @param date The appointment date as a string (format: yyyy-MM-dd)
     * @param patientName The patient name to filter by (can be "null" or actual name)
     * @param token The JWT token for authentication
     * @return ResponseEntity containing appointments or error message
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate token for doctor role
            Map<String, String> validationResult = service.validateToken(token, "doctor");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Parse date from string
            LocalDate appointmentDate = LocalDate.parse(date);

            // Handle "null" string as null
            String patientNameFilter = ("null".equalsIgnoreCase(patientName)) ? null : patientName;

            // Fetch appointments using appointmentService
            Map<String, Object> appointmentsResult = appointmentService.getAppointment(
                    patientNameFilter, appointmentDate, token);

            // Check if there's an error in the result
            if (appointmentsResult.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appointmentsResult);
            }

            return ResponseEntity.status(HttpStatus.OK).body(appointmentsResult);

        } catch (Exception e) {
            response.put("error", "Error retrieving appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP POST requests to create a new appointment.
     * Only patients can book appointments.
     * 
     * @param appointment The Appointment object containing appointment details
     * @param token The JWT token for authentication
     * @return ResponseEntity with success message or error message
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate token for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Validate the appointment using service.validateAppointment()
            int appointmentValidation = service.validateAppointment(appointment);

            if (appointmentValidation == -1) {
                response.put("message", "Invalid doctor ID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if (appointmentValidation == 0) {
                response.put("message", "Appointment time is not available");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Book the appointment
            int bookingResult = appointmentService.bookAppointment(appointment);

            if (bookingResult == 1) {
                response.put("message", "Appointment booked successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Error booking appointment");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Error booking appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP PUT requests to modify an existing appointment.
     * Only patients can update appointments.
     * 
     * @param appointment The Appointment object with updated information
     * @param token The JWT token for authentication
     * @return ResponseEntity with success or failure message
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate token for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Update the appointment using appointmentService
            return appointmentService.updateAppointment(appointment);

        } catch (Exception e) {
            response.put("message", "Error updating appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP DELETE requests to cancel a specific appointment.
     * Only patients can cancel appointments, and only their own appointments.
     * 
     * @param id The ID of the appointment to cancel
     * @param token The JWT token for authentication
     * @return ResponseEntity with success or failure message
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate token for patient role
            Map<String, String> validationResult = service.validateToken(token, "patient");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Cancel the appointment using appointmentService
            return appointmentService.cancelAppointment(id, token);

        } catch (Exception e) {
            response.put("message", "Error cancelling appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
