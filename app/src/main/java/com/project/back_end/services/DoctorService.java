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

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

/**
 * DoctorService
 * 
 * Service class to manage operations related to doctors, including retrieving availability,
 * saving, updating, deleting, and validating doctors.
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    // Constructor injection for dependencies
    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Fetch the available slots for a specific doctor on a given date.
     * Filters out already booked slots from the doctor's available times.
     * 
     * @param doctorId The ID of the doctor
     * @param date The date for which availability is needed
     * @return A list of available time slots for the doctor on the specified date
     */
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (doctorOpt.isEmpty()) {
                return new ArrayList<>();
            }

            Doctor doctor = doctorOpt.get();
            List<String> availableTimes = new ArrayList<>(doctor.getAvailableTimes());

            // Calculate start and end of the day
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            // Fetch appointments for the doctor on the specified date
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                    doctorId, start, end);

            // Extract booked time slots
            List<String> bookedSlots = new ArrayList<>();
            for (Appointment appointment : appointments) {
                LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
                // Format as "HH:mm - HH:mm" (1 hour slot)
                String bookedSlot = String.format("%02d:%02d - %02d:%02d",
                        appointmentTime.getHour(), appointmentTime.getMinute(),
                        appointmentTime.plusHours(1).getHour(), appointmentTime.plusHours(1).getMinute());
                bookedSlots.add(bookedSlot);
            }

            // Filter out booked slots from available times
            availableTimes.removeAll(bookedSlots);
            return availableTimes;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Save a new doctor to the database.
     * Checks if a doctor with the same email already exists before saving.
     * 
     * @param doctor The doctor object to save
     * @return 1 for success, -1 if the doctor already exists, 0 for internal errors
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            // Check if doctor already exists by email
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1; // Doctor already exists
            }

            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Update the details of an existing doctor.
     * Checks if the doctor exists by ID before updating.
     * 
     * @param doctor The doctor object with updated details
     * @return 1 for success, -1 if doctor not found, 0 for internal errors
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctorOpt = doctorRepository.findById(doctor.getId());
            if (existingDoctorOpt.isEmpty()) {
                return -1; // Doctor not found
            }

            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Retrieve a list of all doctors.
     * 
     * @return A list of all doctors
     */
    @Transactional
    public List<Doctor> getDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Delete a doctor by ID.
     * Deletes all associated appointments before deleting the doctor.
     * 
     * @param id The ID of the doctor to be deleted
     * @return 1 for success, -1 if doctor not found, 0 for internal errors
     */
    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctorOpt = doctorRepository.findById(id);
            if (doctorOpt.isEmpty()) {
                return -1; // Doctor not found
            }

            // Delete all associated appointments
            appointmentRepository.deleteAllByDoctorId(id);

            // Delete the doctor
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    /**
     * Validate a doctor's login credentials.
     * 
     * @param login The login object containing email (identifier) and password
     * @return ResponseEntity with a token if valid, or an error message if not
     */
    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            // Find doctor by email (identifier)
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

            if (doctor == null) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Verify password
            if (!doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Generate token
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("message", "Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Find doctors by their name (partial match).
     * 
     * @param name The name of the doctor to search for
     * @return A map with the list of doctors matching the name
     */
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Doctor> doctors = doctorRepository.findByNameLike(name);
            response.put("doctors", doctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error finding doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter doctors by name, specialty, and availability during AM/PM.
     * 
     * @param name Doctor's name
     * @param specialty Doctor's specialty
     * @param amOrPm Time of day: AM or PM
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Filter by name and specialty
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);

            // Filter by time
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

            response.put("doctors", filteredDoctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter doctors by name and their availability during AM/PM.
     * 
     * @param name Doctor's name
     * @param amOrPm Time of day: AM or PM
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Filter by name
            List<Doctor> doctors = doctorRepository.findByNameLike(name);

            // Filter by time
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

            response.put("doctors", filteredDoctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter doctors by name and specialty.
     * 
     * @param name Doctor's name
     * @param specialty Doctor's specialty
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
            response.put("doctors", doctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter doctors by specialty and their availability during AM/PM.
     * 
     * @param specialty Doctor's specialty
     * @param amOrPm Time of day: AM or PM
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Filter by specialty
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

            // Filter by time
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

            response.put("doctors", filteredDoctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter doctors by specialty.
     * 
     * @param specialty Doctor's specialty
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
            response.put("doctors", doctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Filter all doctors by their availability during AM/PM.
     * 
     * @param amOrPm Time of day: AM or PM
     * @return A map with the filtered list of doctors
     */
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all doctors
            List<Doctor> doctors = doctorRepository.findAll();

            // Filter by time
            List<Doctor> filteredDoctors = filterDoctorByTime(doctors, amOrPm);

            response.put("doctors", filteredDoctors);
            return response;
        } catch (Exception e) {
            response.put("error", "Error filtering doctors: " + e.getMessage());
            return response;
        }
    }

    /**
     * Private helper method to filter a list of doctors by their available times (AM/PM).
     * 
     * @param doctors The list of doctors to filter
     * @param amOrPm Time of day: AM or PM
     * @return A filtered list of doctors
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filteredDoctors = new ArrayList<>();

        for (Doctor doctor : doctors) {
            List<String> availableTimes = doctor.getAvailableTimes();
            if (availableTimes == null || availableTimes.isEmpty()) {
                continue;
            }

            boolean hasTimeSlot = false;
            for (String timeSlot : availableTimes) {
                // Check if time slot contains AM or PM indicators
                // Or parse the time to determine if it's AM or PM
                if (isTimeSlotInPeriod(timeSlot, amOrPm)) {
                    hasTimeSlot = true;
                    break;
                }
            }

            if (hasTimeSlot) {
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

    /**
     * Helper method to check if a time slot falls within the specified period (AM/PM).
     * 
     * @param timeSlot The time slot string (e.g., "09:00 - 10:00" or "09:00 AM - 10:00 AM")
     * @param amOrPm The period: "AM" or "PM"
     * @return true if the time slot falls within the specified period
     */
    private boolean isTimeSlotInPeriod(String timeSlot, String amOrPm) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return false;
        }

        // Check if time slot already contains AM/PM indicator
        String upperTimeSlot = timeSlot.toUpperCase();
        if (upperTimeSlot.contains("AM") || upperTimeSlot.contains("PM")) {
            return upperTimeSlot.contains(amOrPm.toUpperCase());
        }

        // Parse time slot (format: "HH:mm - HH:mm" or "HH:mm-HH:mm")
        try {
            String[] parts = timeSlot.split("\\s*-\\s*");
            if (parts.length >= 1) {
                String startTime = parts[0].trim();
                String[] timeParts = startTime.split(":");
                if (timeParts.length >= 1) {
                    int hour = Integer.parseInt(timeParts[0]);
                    
                    // Determine if it's AM or PM
                    boolean isAM = (hour >= 0 && hour < 12);
                    boolean isPM = (hour >= 12 && hour < 24);
                    
                    if (amOrPm.equalsIgnoreCase("AM") && isAM) {
                        return true;
                    }
                    if (amOrPm.equalsIgnoreCase("PM") && isPM) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // If parsing fails, check if time slot string contains the period
            return timeSlot.toUpperCase().contains(amOrPm.toUpperCase());
        }

        return false;
    }
}
