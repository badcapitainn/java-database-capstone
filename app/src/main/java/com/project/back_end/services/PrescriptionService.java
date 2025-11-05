package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

/**
 * PrescriptionService
 * 
 * Service class to handle the creation and retrieval of prescriptions.
 * It provides two key functionalities: saving a new prescription and 
 * retrieving an existing prescription based on an appointment ID.
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    // Constructor injection for dependencies
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * Saves a prescription to the database.
     * Checks if a prescription already exists for the same appointment before saving.
     * 
     * @param prescription The prescription object to be saved
     * @return ResponseEntity with a message indicating the result of the save operation
     */
    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();

        try {
            // Check if a prescription already exists for this appointment
            List<Prescription> existingPrescriptions = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            
            if (!existingPrescriptions.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Save the prescription
            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Error saving prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Retrieves the prescription associated with a specific appointment ID.
     * 
     * @param appointmentId The appointment ID whose associated prescription is to be retrieved
     * @return ResponseEntity containing the prescription details or an error message
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch prescriptions for the appointment ID
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptions.isEmpty()) {
                response.put("message", "No prescription found for this appointment");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // If multiple prescriptions exist, return the first one (or all if needed)
            // For simplicity, we'll return the first prescription
            Prescription prescription = prescriptions.get(0);

            // Add prescription details to response
            response.put("prescription", prescription);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("error", "Error retrieving prescription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
