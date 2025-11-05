package com.project.back_end.DTO;

/**
 * Login DTO
 * 
 * Data Transfer Object (DTO) to represent login request data.
 * This class encapsulates user credentials submitted from the frontend
 * during login operations.
 * 
 * The identifier field can be:
 * - Email address (for Doctor/Patient users)
 * - Username (for Admin users)
 * 
 * Note: This class is typically used in @RequestBody parameters inside controller methods.
 * Do not add any persistence annotations (@Entity, @Id, etc.) as this DTO is used only
 * for authentication input and is not stored in the database.
 */
public class Login {

    /**
     * The unique identifier of the user attempting to log in.
     * Can be an email address (for Doctor/Patient) or username (for Admin).
     */
    private String identifier;

    /**
     * The password provided by the user for authentication.
     */
    private String password;

    // Default constructor (required for JSON deserialization)
    public Login() {
    }

    // Parameterized constructor for convenience
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Standard getter and setter methods to enable deserialization of the login request body

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
