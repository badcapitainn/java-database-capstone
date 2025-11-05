// patientServices.js

// Import the API Base URL
import { API_BASE_URL } from "../config/config.js";

// Set the Base Patient API Endpoint
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Function to handle patient signup
 * Accepts a data object with patient details (name, email, password, etc.)
 * @param {Object} data - Patient details object containing name, email, password, phone, address
 * @returns {Promise<Object>} Object with success status and message
 */
export async function patientSignup(data) {
    try {
        // Send a POST request to the signup endpoint
        const response = await fetch(`${PATIENT_API}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            // Include the patient details as JSON in the request body
            body: JSON.stringify(data)
        });

        // Wait for the response and extract the message
        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.message || "Signup failed");
        }

        // Return a structured object with success and message properties
        return { success: response.ok, message: result.message };
    } catch (error) {
        // Handle any failures with a try-catch block and return an appropriate error message
        console.error("Error :: patientSignup :: ", error);
        return { success: false, message: error.message || "An error occurred during signup" };
    }
}

/**
 * Function for patient login
 * Accepts login credentials (typically email and password)
 * @param {Object} data - Login credentials object with email and password
 * @returns {Promise<Response>} The full fetch response so the frontend can check status, extract token, etc.
 */
export async function patientLogin(data) {
    // Logging the input data can help during development (but should be removed in production)
    console.log("patientLogin :: ", data);
    
    // Send a POST request to the login endpoint
    // Include headers indicating JSON content and pass the login data in the body
    return await fetch(`${PATIENT_API}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });
}

/**
 * Function to fetch logged-in patient data
 * Takes an authentication token (from localStorage) and retrieves the patient's details
 * @param {string} token - Authentication token from localStorage
 * @returns {Promise<Object|null>} Patient object if successful, null if request fails
 */
export async function getPatientData(token) {
    try {
        // Send a GET request using this token to retrieve the patient's details (name, id, etc.)
        const response = await fetch(`${PATIENT_API}/${token}`);
        const data = await response.json();
        
        // Return the patient object if successful
        if (response.ok) {
            return data.patient || data;
        }
        
        // Handle any errors gracefully and return null if the request fails
        return null;
    } catch (error) {
        console.error("Error fetching patient details:", error);
        return null;
    }
}

/**
 * Function to fetch patient appointments
 * The Backend API for fetching the patient record (visible in Doctor Dashboard) 
 * and Appointments (visible in Patient Dashboard) are the same based on user (patient/doctor)
 * @param {number|string} id - Patient's unique identifier
 * @param {string} token - Authentication token
 * @param {string} user - String indicating who's requesting (e.g., "patient" or "doctor")
 * @returns {Promise<Array|null>} Appointments array if successful, null if request fails
 */
export async function getPatientAppointments(id, token, user) {
    try {
        // Construct a dynamic API URL that works for both dashboards — doctor and patient
        const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
        const data = await response.json();
        
        // Send a GET request and return the appointments array
        if (response.ok) {
            return data.appointments || data || [];
        }
        
        // If unsuccessful, log the error and return null
        console.error("Failed to fetch appointments:", response.statusText);
        return null;
    } catch (error) {
        console.error("Error fetching patient appointments:", error);
        return null;
    }
}

/**
 * Function to filter appointments
 * Accepts condition (like "pending" or "consulted"), name, and a token
 * @param {string} condition - Filter condition (e.g., "pending", "consulted")
 * @param {string} name - Patient or doctor name to filter by
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Object with filtered appointments array or empty array on error
 */
export async function filterAppointments(condition, name, token) {
    try {
        // Send a GET request to a filtered endpoint
        const response = await fetch(`${PATIENT_API}/filter/${condition}/${name}/${token}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        // Return the list of filtered appointments if the request is successful
        if (response.ok) {
            const data = await response.json();
            return data;
        } else {
            // Return an empty list if something fails, and log errors for easier debugging
            console.error("Failed to filter appointments:", response.statusText);
            return { appointments: [] };
        }
    } catch (error) {
        // Log errors for easier debugging
        console.error("Error filtering appointments:", error);
        // Alert the user if the error is unexpected
        alert("Something went wrong while filtering appointments!");
        // Return an empty list to prevent frontend crashes
        return { appointments: [] };
    }
}
