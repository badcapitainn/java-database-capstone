package com.project.back_end.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * TokenService
 * 
 * Service class to handle JWT token generation, extraction, and validation.
 * This service provides secure authentication mechanisms for admin, doctor, and patient users.
 */
@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Constructor injection for dependencies
    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves the signing key used for JWT token signing.
     * Uses the secret from application configuration (application.properties) to generate
     * a signing key using Keys.hmacShaKeyFor().
     * 
     * @return The SecretKey used for signing the JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generates a JWT token for a given user's identifier.
     * The token includes the user's identifier as the subject, the current date as the issued date,
     * and sets an expiration of 7 days.
     * 
     * @param identifier The unique identifier for the user (username for Admin, email for Doctor and Patient)
     * @return The generated JWT token as a String
     */
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (7 * 24 * 60 * 60 * 1000L)); // 7 days in milliseconds

        return Jwts.builder()
                .subject(identifier)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the identifier (subject) from a JWT token.
     * The method parses the token using Jwts.parser() and extracts the subject
     * (which can be email or username).
     * 
     * @param token The JWT token from which the identifier is to be extracted
     * @return The identifier extracted from the token
     */
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error extracting identifier from token: " + e.getMessage(), e);
        }
    }

    /**
     * Convenience method that extracts the email (identifier) from a JWT token.
     * For doctors and patients, the identifier is the email. For admins, it's the username.
     * This method is provided for backward compatibility with existing code.
     * 
     * @param token The JWT token from which the email is to be extracted
     * @return The email/identifier extracted from the token
     */
    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    /**
     * Validates the JWT token for a given user type (admin, doctor, or patient).
     * The method extracts the identifier from the token and checks if a corresponding user
     * exists in the database. The validation checks depend on the provided user type.
     * 
     * @param token The JWT token to be validated
     * @param user The type of user (admin, doctor, or patient)
     * @return true if the token is valid for the specified user type, false if invalid or expired
     */
    public boolean validateToken(String token, String user) {
        try {
            // Extract the identifier from the token
            String identifier = extractIdentifier(token);

            // Validate based on user type
            if ("admin".equalsIgnoreCase(user)) {
                // For admin, identifier is the username
                Admin admin = adminRepository.findByUsername(identifier);
                return admin != null;
            } else if ("doctor".equalsIgnoreCase(user)) {
                // For doctor, identifier is the email
                Doctor doctor = doctorRepository.findByEmail(identifier);
                return doctor != null;
            } else if ("patient".equalsIgnoreCase(user)) {
                // For patient, identifier is the email
                Patient patient = patientRepository.findByEmail(identifier);
                return patient != null;
            }

            // Unknown user type
            return false;

        } catch (Exception e) {
            // Token is invalid, expired, or user doesn't exist
            return false;
        }
    }
}
