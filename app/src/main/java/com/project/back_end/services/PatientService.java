package com.project.back_end.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * PatientService
 * 
 * Service class to handle various operations related to patients, such as creating a patient,
 * fetching their appointments, and filtering those appointments based on specific conditions
 * (for example, past, future, by doctor).
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor injection for dependencies
    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Creates a new patient in the database.
     * 
     * @param patient The patient object to be saved
     * @return 1 on success, 0 on failure
     */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1; // Success
        } catch (Exception e) {
            // Log the error if needed
            return 0; // Failure
        }
    }

    /**
     * Retrieves a list of appointments for a specific patient.
     * Checks if the provided patient ID matches the one decoded from the token (by email).
     * 
     * @param id The patient's ID
     * @param token The JWT token containing the email
     * @return ResponseEntity containing a list of appointments or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract email from token
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check if the provided patient ID matches the one from the token
            if (!patient.getId().equals(id)) {
                response.put("error", "Unauthorized: Patient ID mismatch");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Retrieve appointments for the patient
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);

            // Convert appointments to AppointmentDTO objects
            List<AppointmentDTO> appointmentDTOs = convertToDTOs(appointments);

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error retrieving appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters appointments by condition (past or future) for a specific patient.
     * 
     * @param condition The condition to filter by (past or future)
     * @param id The patient's ID
     * @return ResponseEntity containing the filtered appointments or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Determine status based on condition
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1; // Completed
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0; // Scheduled
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Retrieve appointments filtered by condition
            List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

            // Convert appointments to AppointmentDTO objects
            List<AppointmentDTO> appointmentDTOs = convertToDTOs(appointments);

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters the patient's appointments by doctor's name.
     * 
     * @param name The name of the doctor
     * @param patientId The ID of the patient
     * @return ResponseEntity containing the filtered appointments or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Filter appointments by doctor name and patient ID
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

            // Convert appointments to AppointmentDTO objects
            List<AppointmentDTO> appointmentDTOs = convertToDTOs(appointments);

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Filters the patient's appointments by doctor's name and appointment condition (past or future).
     * 
     * @param condition The condition to filter by (past or future)
     * @param name The name of the doctor
     * @param patientId The ID of the patient
     * @return ResponseEntity containing the filtered appointments or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Determine status based on condition
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1; // Completed
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0; // Scheduled
            } else {
                response.put("error", "Invalid condition. Use 'past' or 'future'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Filter appointments by doctor name, patient ID, and status
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

            // Convert appointments to AppointmentDTO objects
            List<AppointmentDTO> appointmentDTOs = convertToDTOs(appointments);

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Fetches the patient's details based on the provided JWT token.
     * 
     * @param token The JWT token containing the email
     * @return ResponseEntity containing the patient's details or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract email from token
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Return patient details (excluding password for security)
            Map<String, Object> patientDetails = new HashMap<>();
            patientDetails.put("id", patient.getId());
            patientDetails.put("name", patient.getName());
            patientDetails.put("email", patient.getEmail());
            patientDetails.put("phone", patient.getPhone());
            patientDetails.put("address", patient.getAddress());

            response.put("patient", patientDetails);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error retrieving patient details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper method to convert a list of Appointment entities to AppointmentDTO objects.
     * 
     * @param appointments The list of Appointment entities
     * @return A list of AppointmentDTO objects
     */
    private List<AppointmentDTO> convertToDTOs(List<Appointment> appointments) {
        List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

        for (Appointment appointment : appointments) {
            AppointmentDTO dto = new AppointmentDTO(
                    appointment.getId(),
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName(),
                    appointment.getPatient().getId(),
                    appointment.getPatient().getName(),
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getPhone(),
                    appointment.getPatient().getAddress(),
                    appointment.getAppointmentTime(),
                    appointment.getStatus()
            );
            appointmentDTOs.add(dto);
        }

        return appointmentDTOs;
    }
}
