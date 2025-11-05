// doctorServices.js

// Import API Base URL
import { API_BASE_URL } from "../config/config.js";

// Set Doctor API Endpoint
const DOCTOR_API = API_BASE_URL + '/doctor';

/**
 * Function to get all doctors
 * Sends a GET request to fetch all doctors from the API
 * @returns {Promise<Array>} Array of doctor objects, or empty array on error
 */
export async function getDoctors() {
    try {
        // Send GET request to the doctor endpoint
        const response = await fetch(DOCTOR_API);
        
        // Check if response is OK
        if (response.ok) {
            // Extract and return the list of doctors from the response JSON
            const data = await response.json();
            return data.doctors || data || [];
        } else {
            console.error("Failed to fetch doctors:", response.statusText);
            return [];
        }
    } catch (error) {
        // Handle any errors using try-catch block
        console.error("Error fetching doctors:", error);
        // Return an empty list if something goes wrong to avoid breaking the frontend
        return [];
    }
}

/**
 * Function to delete a doctor
 * Takes the doctor's unique id and an authentication token for security
 * @param {number|string} id - Doctor's unique identifier
 * @param {string} token - Authentication token
 * @returns {Promise<Object>} Object with success status and message
 */
export async function deleteDoctor(id, token) {
    try {
        // Construct the full endpoint URL using the ID and token
        const url = `${DOCTOR_API}/${id}/${token}`;
        
        // Send a DELETE request to that endpoint
        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Parse the JSON response
        const data = await response.json();
        
        // Return a success status and message
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor deleted successfully" : "Failed to delete doctor")
        };
    } catch (error) {
        // Catch and handle any errors to prevent frontend crashes
        console.error("Error deleting doctor:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}

/**
 * Function to save (add) a new doctor
 * Accepts a doctor object containing all doctor details and an authentication token
 * @param {Object} doctor - Doctor object with name, email, specialty, phone, password, availableTimes, etc.
 * @param {string} token - Authentication token for Admin
 * @returns {Promise<Object>} Object with success status and message
 */
export async function saveDoctor(doctor, token) {
    try {
        // Construct the endpoint URL with token
        const url = `${DOCTOR_API}/${token}`;
        
        // Send a POST request with headers specifying JSON data
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            // Include the doctor data in the request body (converted to JSON)
            body: JSON.stringify(doctor)
        });

        // Parse the JSON response
        const data = await response.json();
        
        // Return a structured response with success and message
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor added successfully" : "Failed to add doctor")
        };
    } catch (error) {
        // Catch and log any errors to help during debugging
        console.error("Error saving doctor:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}

/**
 * Function to filter doctors
 * Accepts parameters like name, time, and specialty to filter doctor records
 * @param {string|null} name - Doctor name to filter by (can be null or empty)
 * @param {string|null} time - Time availability to filter by (can be null or empty)
 * @param {string|null} specialty - Specialty to filter by (can be null or empty)
 * @returns {Promise<Object>} Object with filtered doctors array or empty array on error
 */
export async function filterDoctors(name, time, specialty) {
    try {
        // Normalize empty values to null or empty strings
        const nameParam = name || "";
        const timeParam = time || "";
        const specialtyParam = specialty || "";

        // Construct a GET request URL by passing these values as route parameters
        const url = `${DOCTOR_API}/filter/${nameParam}/${timeParam}/${specialtyParam}`;
        
        // Send a GET request to retrieve matching doctor records
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Check if response is OK
        if (response.ok) {
            // Return the filtered list of doctors
            const data = await response.json();
            return data.doctors || data || [];
        } else {
            // Log error if request fails
            console.error("Failed to filter doctors:", response.statusText);
            // Return an empty list if none are found
            return [];
        }
    } catch (error) {
        // Handle cases where no filters are applied or errors occur
        // Use error handling to alert the user if something fails
        console.error("Error filtering doctors:", error);
        alert("An error occurred while filtering doctors. Please try again.");
        // Return an empty list to prevent frontend crashes
        return [];
    }
}
