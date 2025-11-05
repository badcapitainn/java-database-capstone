package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

/**
 * DoctorController
 * 
 * REST controller for handling all operations related to the Doctor entity.
 * Allows adding, updating, deleting, fetching, and filtering doctors.
 * Also manages login functionality for doctors and validates their tokens.
 */
@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    // Constructor injection for dependencies
    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * Handles HTTP GET requests to check a specific doctor's availability on a given date.
     * 
     * @param user Role of the user (doctor, patient, admin, etc.)
     * @param doctorId The unique ID of the doctor
     * @param date The date for which the availability needs to be fetched (format: yyyy-MM-dd)
     * @param token The authentication token for validating the user
     * @return ResponseEntity containing the doctor's availability or an error message
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken()
            Map<String, String> validationResult = service.validateToken(token, user);

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("error", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Parse date from string
            LocalDate appointmentDate = LocalDate.parse(date);

            // Fetch the doctor's availability using doctorService.getDoctorAvailability()
            List<String> availability = doctorService.getDoctorAvailability(doctorId, appointmentDate);

            response.put("availability", availability);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error fetching doctor availability: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP GET requests to retrieve a list of all doctors.
     * 
     * @return ResponseEntity containing a list of doctors in the response map
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetches a list of all doctors from the doctorService.getDoctors() method
            List<Doctor> doctors = doctorService.getDoctors();

            response.put("doctors", doctors);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error fetching doctors: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP POST requests to register a new doctor.
     * Only admins can add doctors.
     * 
     * @param doctor The doctor details to be added
     * @param token The authentication token for validation
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate the token with service.validateToken() for admin role
            Map<String, String> validationResult = service.validateToken(token, "admin");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Attempt to save the doctor using doctorService.saveDoctor()
            int saveResult = doctorService.saveDoctor(doctor);

            if (saveResult == 1) {
                // Success
                response.put("message", "Doctor added to db");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else if (saveResult == -1) {
                // Conflict - Doctor already exists
                response.put("message", "Doctor already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                // Internal error
                response.put("message", "Some internal error occurred");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP POST requests for doctor login.
     * 
     * @param login The login details (email as identifier, password)
     * @return ResponseEntity with token if valid, or error message if not
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        // Calls doctorService.validateDoctor() to validate the doctor's credentials
        return doctorService.validateDoctor(login);
    }

    /**
     * Handles HTTP PUT requests to update an existing doctor's information.
     * Only admins can update doctors.
     * 
     * @param doctor The doctor object with updated details
     * @param token The authentication token for validation
     * @return ResponseEntity with success or error message
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() for admin role
            Map<String, String> validationResult = service.validateToken(token, "admin");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Attempt to update the doctor using doctorService.updateDoctor()
            int updateResult = doctorService.updateDoctor(doctor);

            if (updateResult == 1) {
                // Success
                response.put("message", "Doctor updated");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else if (updateResult == -1) {
                // Not found - Doctor doesn't exist
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                // Internal error
                response.put("message", "Some internal error occurred");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP DELETE requests to remove a doctor by ID.
     * Only admins can delete doctors.
     * 
     * @param id The ID of the doctor to be deleted
     * @param token The authentication token for validation
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        try {
            // Validate the token using service.validateToken() for admin role
            Map<String, String> validationResult = service.validateToken(token, "admin");

            // If validation result is not empty, token is invalid
            if (!validationResult.isEmpty()) {
                response.put("message", validationResult.get("error"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Attempt to delete the doctor using doctorService.deleteDoctor()
            int deleteResult = doctorService.deleteDoctor(id);

            if (deleteResult == 1) {
                // Success
                response.put("message", "Doctor deleted successfully");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else if (deleteResult == -1) {
                // Not found - Doctor doesn't exist
                response.put("message", "Doctor not found with id");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                // Internal error
                response.put("message", "Some internal error occurred");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("message", "Some internal error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Handles HTTP GET requests to filter doctors based on name, time, and specialty.
     * 
     * @param name The name of the doctor (can be partial, can be "null")
     * @param time The available time for filtering (AM/PM, can be "null")
     * @param speciality The specialty of the doctor (can be "null")
     * @return ResponseEntity containing a map of filtered doctor data
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        try {
            // Handle "null" strings as null
            String nameFilter = ("null".equalsIgnoreCase(name)) ? null : name;
            String timeFilter = ("null".equalsIgnoreCase(time)) ? null : time;
            String specialtyFilter = ("null".equalsIgnoreCase(speciality)) ? null : speciality;

            // Uses service.filterDoctor() to filter doctors based on the given parameters
            Map<String, Object> filteredDoctors = service.filterDoctor(nameFilter, specialtyFilter, timeFilter);

            return ResponseEntity.status(HttpStatus.OK).body(filteredDoctors);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
