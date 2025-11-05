package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Doctor;

/**
 * DoctorRepository
 * 
 * Repository interface for Doctor entities that extends JpaRepository to provide
 * basic CRUD operations and custom query methods for searching doctors by name, email, and specialty.
 * 
 * This repository supports:
 * - Basic CRUD operations (save, delete, update, find) inherited from JpaRepository
 * - Pagination and sorting features from JpaRepository
 * - Custom query methods using Spring Data JPA conventions and @Query annotations
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by their email address.
     * Uses Spring Data JPA naming convention (no @Query needed).
     * 
     * @param email The email address of the doctor
     * @return Doctor entity matching the email, or null if not found
     */
    Doctor findByEmail(String email);

    /**
     * Find doctors by partial name match.
     * Uses @Query with LIKE and CONCAT for flexible pattern matching.
     * 
     * @param name The partial name to search for
     * @return List of doctors whose name contains the provided search string
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Filter doctors by partial name and exact specialty (case-insensitive).
     * Uses @Query with LOWER, CONCAT, and LIKE for case-insensitive matching.
     * 
     * @param name The partial name to search for (case-insensitive)
     * @param specialty The specialty to match exactly (case-insensitive)
     * @return List of doctors matching the name and specialty criteria
     */
    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name,
            @Param("specialty") String specialty);

    /**
     * Find doctors by specialty, ignoring case.
     * Uses Spring Data JPA naming convention (IgnoreCase suffix).
     * 
     * @param specialty The specialty to search for (case-insensitive)
     * @return List of doctors with the specified specialty
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
