package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;

/**
 * AppointmentRepository
 * 
 * Repository interface for Appointment entities that extends JpaRepository to provide
 * basic CRUD operations and custom query methods for advanced appointment search and filtering.
 * 
 * This repository supports:
 * - Basic CRUD operations (save, delete, update, find) inherited from JpaRepository
 * - Pagination and sorting features from JpaRepository
 * - Custom query methods using Spring Data JPA conventions and @Query annotations
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Retrieve appointments for a doctor within a given time range.
     * Uses LEFT JOIN FETCH to include doctor and availability info to avoid lazy loading issues.
     * 
     * @param doctorId The ID of the doctor
     * @param start The start of the time range (inclusive)
     * @param end The end of the time range (inclusive)
     * @return List of appointments for the doctor within the specified time range
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Filter appointments by doctor ID, partial patient name (case-insensitive), and time range.
     * Uses LEFT JOIN FETCH to include patient and doctor details to avoid lazy loading issues.
     * 
     * @param doctorId The ID of the doctor
     * @param patientName The partial patient name to search for (case-insensitive)
     * @param start The start of the time range (inclusive)
     * @param end The end of the time range (inclusive)
     * @return List of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Delete all appointments related to a specific doctor.
     * Uses @Modifying and @Transactional to enable delete operation.
     * 
     * @param doctorId The ID of the doctor whose appointments should be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * Find all appointments for a specific patient.
     * Uses Spring Data JPA naming convention.
     * 
     * @param patientId The ID of the patient
     * @return List of appointments for the patient
     */
    List<Appointment> findByPatientId(Long patientId);

    /**
     * Retrieve appointments for a patient by status, ordered by appointment time in ascending order.
     * Uses Spring Data JPA naming convention.
     * 
     * @param patientId The ID of the patient
     * @param status The status of the appointment (e.g., 0 = Scheduled, 1 = Completed)
     * @return List of appointments for the patient with the specified status, ordered by appointment time
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    /**
     * Search appointments by partial doctor name (case-insensitive) and patient ID.
     * Uses @Query with LOWER and CONCAT for case-insensitive partial matching.
     * 
     * @param doctorName The partial doctor name to search for (case-insensitive)
     * @param patientId The ID of the patient
     * @return List of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId);

    /**
     * Filter appointments by doctor name (case-insensitive partial match), patient ID, and status.
     * Uses @Query with LOWER, CONCAT, and additional filtering on status.
     * 
     * @param doctorName The partial doctor name to search for (case-insensitive)
     * @param patientId The ID of the patient
     * @param status The status of the appointment (e.g., 0 = Scheduled, 1 = Completed)
     * @return List of appointments matching all criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId,
            @Param("status") int status);
}
