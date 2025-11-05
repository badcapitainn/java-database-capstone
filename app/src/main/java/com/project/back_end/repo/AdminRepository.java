package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Admin;

/**
 * AdminRepository
 * 
 * Repository interface for Admin entities that extends JpaRepository to provide
 * basic CRUD operations without needing to implement the methods manually.
 * 
 * This repository supports:
 * - Basic CRUD operations (save, delete, update, find) inherited from JpaRepository
 * - Pagination and sorting features from JpaRepository
 * - Custom query methods using Spring Data JPA conventions
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Find an admin by their username.
     * 
     * This method uses Spring Data JPA's query method convention.
     * Spring Data JPA automatically generates the implementation based on the method name.
     * 
     * @param username The username to search for
     * @return Admin entity that matches the provided username, or null if not found
     */
    Admin findByUsername(String username);
}
