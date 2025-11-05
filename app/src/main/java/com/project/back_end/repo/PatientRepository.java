package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Patient;

/**
 * PatientRepository
 * 
 * Repository interface for Patient entities that extends JpaRepository to provide
 * basic CRUD operations and custom query methods for retrieving patients using
 * their email or phone number for identification and validation.
 * 
 * This repository supports:
 * - Basic CRUD operations (save, delete, update, find) inherited from JpaRepository
 * - Pagination and sorting features from JpaRepository
 * - Custom query methods using Spring Data JPA naming conventions
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email address.
     * Uses Spring Data JPA naming convention (no @Query needed).
     * 
     * @param email The email address of the patient
     * @return Patient entity matching the email, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Find a patient using either email or phone number.
     * Uses Spring Data JPA naming convention with Or operator for compound queries.
     * This allows flexibility for patient identification - can search by either identifier.
     * 
     * @param email The email address of the patient
     * @param phone The phone number of the patient
     * @return Patient entity matching either the email or phone number, or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);
}
