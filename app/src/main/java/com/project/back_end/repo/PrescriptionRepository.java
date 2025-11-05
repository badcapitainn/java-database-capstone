package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Prescription;

/**
 * PrescriptionRepository
 * 
 * Repository interface for Prescription entities that extends MongoRepository to provide
 * basic CRUD operations on MongoDB without implementing methods manually.
 * 
 * This repository supports:
 * - Basic CRUD operations (save, delete, update, find) inherited from MongoRepository
 * - MongoDB-specific query methods using Spring Data MongoDB naming conventions
 * - Custom query methods for fetching prescriptions by appointment ID
 * 
 * Note: This repository uses MongoDB (not a relational database), so it extends
 * MongoRepository instead of JpaRepository.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Find prescriptions associated with a specific appointment.
     * Uses Spring Data MongoDB naming convention (no @Query needed).
     * MongoRepository automatically derives the query from the method name.
     * 
     * @param appointmentId The ID of the appointment to search for
     * @return List of prescriptions associated with the specified appointment
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}
