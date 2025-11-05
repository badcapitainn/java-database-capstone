package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * AppointmentDTO
 * 
 * Data Transfer Object (DTO) to represent appointment data for communication 
 * between backend services and frontend clients. This class helps decouple 
 * frontend requirements from the internal database structure.
 * 
 * Note: This DTO class should not contain persistence annotations like @Entity or @Id.
 * It is meant to simplify and format data transferred to/from the frontend.
 */
public class AppointmentDTO {

    // Core fields
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String patientAddress;
    private LocalDateTime appointmentTime;
    private int status;

    // Derived fields (computed from appointmentTime)
    private LocalDate appointmentDate;
    private LocalTime appointmentTimeOnly;
    private LocalDateTime endTime;

    // Default constructor
    public AppointmentDTO() {
    }

    /**
     * Constructor that initializes all core fields and automatically computes derived fields
     * 
     * @param id - Unique identifier for the appointment
     * @param doctorId - ID of the doctor assigned to the appointment
     * @param doctorName - Full name of the doctor
     * @param patientId - ID of the patient
     * @param patientName - Full name of the patient
     * @param patientEmail - Email address of the patient
     * @param patientPhone - Contact number of the patient
     * @param patientAddress - Residential address of the patient
     * @param appointmentTime - Full date and time of the appointment
     * @param status - Appointment status (e.g., scheduled, completed)
     */
    public AppointmentDTO(Long id, Long doctorId, String doctorName, Long patientId, 
                          String patientName, String patientEmail, String patientPhone, 
                          String patientAddress, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.appointmentTime = appointmentTime;
        this.status = status;

        // Automatically compute derived fields from appointmentTime
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        }
    }

    // Standard getter methods for each field to allow serialization in API responses

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Setters (optional, for flexibility if needed)
    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        // Recompute derived fields when appointmentTime changes
        if (appointmentTime != null) {
            this.appointmentDate = appointmentTime.toLocalDate();
            this.appointmentTimeOnly = appointmentTime.toLocalTime();
            this.endTime = appointmentTime.plusHours(1);
        } else {
            this.appointmentDate = null;
            this.appointmentTimeOnly = null;
            this.endTime = null;
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
