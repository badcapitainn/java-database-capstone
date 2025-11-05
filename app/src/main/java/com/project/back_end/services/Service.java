package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * Service
 * 
 * Central service class that combines multiple functionalities: authentication,
 * validation, and coordination across entities. This class handles authentication
 * for admins and patients, validates appointments, filters doctors and patient
 * appointments, and coordinates between different services.
 */
@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Constructor injection for dependencies
    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Checks the validity of a token for a given user.
     * Returns an empty map if valid, or a map with error message if invalid.
     * 
     * @param token The token to be validated
     * @param user The user role to whom the token belongs (admin, doctor, patient)
     * @return Map with error message if invalid, empty map if valid
     */
    @Transactional
    public Map<String, String> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();

        try {
            // Use tokenService to check if the token is valid
            boolean isValid = tokenService.validateToken(token, user);

            if (!isValid) {
                response.put("error", "Invalid or expired token");
                return response;
            }

            // Return empty map if token is valid
            return response;

        } catch (Exception e) {
            response.put("error", "Error validating token: " + e.getMessage());
            return response;
        }
    }

    /**
     * Validates the login credentials of an admin.
     * 
     * @param receivedAdmin The admin credentials (username and password) to be validated
     * @return ResponseEntity with a generated token if authenticated, or error message if not
     */
    @Transactional
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        try {
            // Find admin by username
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

            if (admin == null) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check if password matches
            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Generate token using admin's username (since admin uses username, not email)
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("message", "Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters doctors based on name, specialty, and available time.
     * If none of the filters are provided, returns all available doctors.
     * 
     * @param name The name of the doctor (can be null)
     * @param specialty The specialty of the doctor (can be null)
     * @param time The available time of the doctor, AM or PM (can be null)
     * @return Map with list of doctors that match the filtering criteria
     */
    @Transactional
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();

        try {
            // If all filters are provided
            if (name != null && !name.trim().isEmpty() && 
                specialty != null && !specialty.trim().isEmpty() && 
                time != null && !time.trim().isEmpty()) {
                return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
            }

            // If name and specialty are provided
            if (name != null && !name.trim().isEmpty() && 
                specialty != null && !specialty.trim().isEmpty()) {
                return doctorService.filterDoctorByNameAndSpecility(name, specialty);
            }

            // If name and time are provided
            if (name != null && !name.trim().isEmpty() && 
                time != null && !time.trim().isEmpty()) {
                return doctorService.filterDoctorByNameAndTime(name, time);
            }

            // If specialty and time are provided
            if (specialty != null && !specialty.trim().isEmpty() && 
                time != null && !time.trim().isEmpty()) {
                return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
            }

            // If only name is provided
            if (name != null && !name.trim().isEmpty()) {
                return doctorService.findDoctorByName(name);
            }

            // If only specialty is provided
            if (specialty != null && !specialty.trim().isEmpty()) {
                return doctorService.filterDoctorBySpecility(specialty);
            }

            // If only time is provided
            if (time != null && !time.trim().isEmpty()) {
                return doctorService.filterDoctorsByTime(time);
            }

            // If no filters are provided, return all doctors
            List<Doctor> doctors = doctorService.getDoctors();
            response.put("doctors", doctors);
            return response;

        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Validates whether an appointment is available based on the doctor's schedule.
     * 
     * @param appointment The appointment to validate
     * @return 1 if the appointment time is valid, 0 if unavailable, -1 if doctor doesn't exist
     */
    @Transactional
    public int validateAppointment(Appointment appointment) {
        try {
            // Check if doctor exists
            Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
            if (doctorOpt.isEmpty()) {
                return -1; // Doctor doesn't exist
            }

            Doctor doctor = doctorOpt.get();

            // Get the date of the appointment
            LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();

            // Get available time slots for the doctor on the specified date
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointmentDate);

            // Get the appointment time in HH:mm format
            LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
            String requestedTimeSlot = String.format("%02d:%02d", appointmentTime.getHour(), appointmentTime.getMinute());

            // Check if the requested time matches any available slot
            for (String availableSlot : availableSlots) {
                // Parse available slot (format: "HH:mm - HH:mm" or "HH:mm-HH:mm")
                String[] parts = availableSlot.split("\\s*-\\s*");
                if (parts.length > 0) {
                    String slotStartTime = parts[0].trim();
                    // Compare just the start time
                    if (slotStartTime.startsWith(requestedTimeSlot) || 
                        requestedTimeSlot.startsWith(slotStartTime.substring(0, Math.min(5, slotStartTime.length())))) {
                        return 1; // Valid appointment time
                    }
                }
            }

            return 0; // Time slot not available

        } catch (Exception e) {
            return 0; // Error occurred, treat as unavailable
        }
    }

    /**
     * Checks whether a patient exists based on their email or phone number.
     * 
     * @param patient The patient to validate
     * @return true if the patient does not exist (valid for registration), false if exists already
     */
    @Transactional
    public boolean validatePatient(Patient patient) {
        try {
            // Check if patient exists by email or phone
            Patient existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

            // If patient is found, return false (patient already exists)
            if (existingPatient != null) {
                return false;
            }

            // If no patient found, return true (patient is valid for new registration)
            return true;

        } catch (Exception e) {
            // On error, assume patient doesn't exist (allow registration)
            return true;
        }
    }

    /**
     * Validates a patient's login credentials (email and password).
     * 
     * @param login The login credentials of the patient (email and password)
     * @return ResponseEntity with a generated token if login is valid, or error message if not
     */
    @Transactional
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            // Find patient by email (identifier)
            Patient patient = patientRepository.findByEmail(login.getIdentifier());

            if (patient == null) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check if password matches
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Generate token using patient's email
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("message", "Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters patient appointments based on certain criteria, such as condition and doctor name.
     * 
     * @param condition The medical condition to filter appointments by (past or future, can be null)
     * @param name The doctor's name to filter appointments by (can be null)
     * @param token The authentication token to identify the patient
     * @return ResponseEntity with the filtered list of patient appointments based on the criteria
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract email from token to identify the patient
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Long patientId = patient.getId();

            // If both condition and name are provided
            if (condition != null && !condition.trim().isEmpty() && 
                name != null && !name.trim().isEmpty()) {
                return patientService.filterByDoctorAndCondition(condition, name, patientId);
            }

            // If only condition is provided
            if (condition != null && !condition.trim().isEmpty()) {
                return patientService.filterByCondition(condition, patientId);
            }

            // If only name is provided
            if (name != null && !name.trim().isEmpty()) {
                return patientService.filterByDoctor(name, patientId);
            }

            // If no filters are provided, return all appointments for the patient
            return patientService.getPatientAppointment(patientId, token);

        } catch (Exception e) {
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
