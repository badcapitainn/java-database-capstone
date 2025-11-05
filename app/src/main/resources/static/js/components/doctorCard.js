// doctorCard.js

// Import necessary functions
import { getPatientData } from '../services/patientServices.js';
import { showBookingOverlay } from '../loggedPatient.js';
import { API_BASE_URL } from '../config/config.js';

// Define DOCTOR_API endpoint
const DOCTOR_API = `${API_BASE_URL}/doctors`;

// Delete doctor function (inline since doctorServices.js may not be fully implemented)
async function deleteDoctor(doctorId, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${doctorId}/${token}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const data = await response.json();
        return {
            success: response.ok,
            message: data.message || (response.ok ? "Doctor deleted successfully" : "Failed to delete doctor")
        };
    } catch (error) {
        console.error("Error deleting doctor:", error);
        return {
            success: false,
            message: "Network error. Please try again later."
        };
    }
}

/**
 * Creates a reusable doctor card element
 * @param {Object} doctor - Doctor object containing name, specialty, email, availableTimes, id, etc.
 * @returns {HTMLElement} The complete doctor card DOM element
 */
export function createDoctorCard(doctor) {
    // Create the main card container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Fetch the user's role
    const role = localStorage.getItem("userRole");

    // Create doctor info section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    // Create doctor name
    const name = document.createElement("h3");
    name.textContent = doctor.name || "Unknown Doctor";

    // Create specialization
    const specialization = document.createElement("p");
    specialization.classList.add("specialty");
    specialization.textContent = `Specialty: ${doctor.specialty || doctor.specialization || "N/A"}`;

    // Create email
    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email || "N/A"}`;

    // Create availability
    const availability = document.createElement("p");
    if (doctor.availableTimes && Array.isArray(doctor.availableTimes) && doctor.availableTimes.length > 0) {
        availability.textContent = `Available: ${doctor.availableTimes.join(", ")}`;
    } else {
        availability.textContent = "Available: Not specified";
    }

    // Append all info elements to the info container
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // Create button container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // Conditionally add buttons based on role
    if (role === "admin") {
        // Admin: Delete button
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");

        removeBtn.addEventListener("click", async () => {
            // 1. Confirm deletion
            const confirmDelete = confirm(`Are you sure you want to delete ${doctor.name}?`);
            if (!confirmDelete) {
                return;
            }

            // 2. Get token from localStorage
            const token = localStorage.getItem("token");
            if (!token) {
                alert("Authentication required. Please log in again.");
                return;
            }

            // 3. Call API to delete
            try {
                const result = await deleteDoctor(doctor.id, token);
                
                // 4. On success: remove the card from the DOM
                if (result.success) {
                    alert(result.message || "Doctor deleted successfully.");
                    card.remove();
                } else {
                    alert(result.message || "Failed to delete doctor.");
                }
            } catch (error) {
                console.error("Error deleting doctor:", error);
                alert("An error occurred while deleting the doctor.");
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        // Patient (not logged in): Book Now button with alert
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        // Logged-in Patient: Book Now button with real booking
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("book-btn");

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            
            if (!token) {
                alert("Session expired. Please log in again.");
                window.location.href = "/pages/patientDashboard.html";
                return;
            }

            try {
                // Fetch patient data
                const patientData = await getPatientData(token);
                
                if (!patientData) {
                    alert("Failed to fetch patient information. Please try again.");
                    return;
                }

                // Show booking overlay
                showBookingOverlay(e, doctor, patientData);
            } catch (error) {
                console.error("Error fetching patient data:", error);
                alert("An error occurred. Please try again.");
            }
        });

        actionsDiv.appendChild(bookNow);
    }

    // Final assembly: Add all created pieces to the card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    // Return the final card
    return card;
}
