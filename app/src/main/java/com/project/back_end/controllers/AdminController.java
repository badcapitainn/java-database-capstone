package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;

/**
 * AdminController
 * 
 * REST controller for handling admin-related operations, specifically admin login.
 * This controller provides an endpoint for admin login validation and token generation.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    // Constructor injection for Service dependency
    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Handles HTTP POST requests for admin login functionality.
     * 
     * @param admin The Admin object containing login credentials (username and password) from the request body
     * @return ResponseEntity containing a Map with token (if successful) or error message (if failed)
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Call validateAdmin method from Service to perform the admin login validation
        return service.validateAdmin(admin);
    }
}
