package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

/**
 * AppointmentService
 * 
 * Service class to handle operations related to appointments, including booking,
 * updating, canceling, and retrieving appointments.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    // Constructor injection for dependencies
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    /**
     * Book a new appointment.
     * Saves the appointment to the database.
     * 
     * @param appointment The appointment object to book
     * @return 1 if successful, 0 if there's an error
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            // Log the error if needed
            return 0;
        }
    }

    /**
     * Update an existing appointment.
     * Validates the appointment exists and is valid before updating.
     * 
     * @param appointment The appointment object with updated information
     * @return ResponseEntity with success or failure message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        try {
            // Check if the appointment exists
            Optional<Appointment> existingAppointmentOpt = appointmentRepository.findById(appointment.getId());
            
            if (existingAppointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment existingAppointment = existingAppointmentOpt.get();

            // Validate the appointment using the service
            int validationResult = service.validateAppointment(appointment);
            
            if (validationResult == -1) {
                response.put("message", "Invalid doctor ID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if (validationResult == 0) {
                response.put("message", "Appointment time is not available");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Check if the patient ID matches
            if (!existingAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("message", "Patient ID mismatch");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Update the appointment
            existingAppointment.setDoctor(appointment.getDoctor());
            existingAppointment.setPatient(appointment.getPatient());
            existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
            existingAppointment.setStatus(appointment.getStatus());

            appointmentRepository.save(existingAppointment);

            response.put("message", "Appointment updated successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("message", "Error updating appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Cancel an existing appointment.
     * Ensures the patient who owns the appointment is trying to cancel it.
     * 
     * @param id The ID of the appointment to cancel
     * @param token The authorization token
     * @return ResponseEntity with success or failure message
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        try {
            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            
            if (appointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Appointment appointment = appointmentOpt.get();

            // Extract patient email from token
            String patientEmail = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(patientEmail);

            // Verify that the patient owns this appointment
            if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
                response.put("message", "Unauthorized: You can only cancel your own appointments");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Delete the appointment
            appointmentRepository.delete(appointment);

            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("message", "Error cancelling appointment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieve a list of appointments for a specific doctor on a specific date.
     * Filters by patient name if provided.
     * 
     * @param pname Patient name to filter by (can be null or empty)
     * @param date The date for appointments
     * @param token The authorization token
     * @return Map containing the list of appointments as AppointmentDTO objects
     */
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Extract doctor email from token
            String doctorEmail = tokenService.extractEmail(token);
            Doctor doctor = doctorRepository.findByEmail(doctorEmail);

            if (doctor == null) {
                response.put("error", "Doctor not found");
                return response;
            }

            // Calculate start and end of the day
            LocalDateTime start = date.atStartOfDay(); // Start of the day (00:00:00)
            LocalDateTime end = date.atTime(LocalTime.MAX); // End of the day (23:59:59)

            // Fetch appointments for the doctor on the specified date
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctor.getId(), start, end);

            // Filter by patient name if provided
            List<Appointment> filteredAppointments = appointments;
            if (pname != null && !pname.trim().isEmpty()) {
                filteredAppointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctor.getId(), pname.trim(), start, end);
            }

            // Convert appointments to AppointmentDTO
            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();
            for (Appointment appointment : filteredAppointments) {
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

            response.put("appointments", appointmentDTOs);
            return response;

        } catch (Exception e) {
            response.put("error", "Error retrieving appointments: " + e.getMessage());
            return response;
        }
    }
}
