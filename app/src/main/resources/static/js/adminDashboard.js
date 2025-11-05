// adminDashboard.js

// Import required modules
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Make openModal available globally for onclick handlers
window.openModal = openModal;

// Event Binding: Add Doctor button
document.addEventListener('DOMContentLoaded', () => {
    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => {
            openModal('addDoctor');
        });
    }

    // Load doctor cards on page load
    loadDoctorCards();

    // Setup search and filter event listeners
    setupFilterListeners();
});

/**
 * Load Doctor Cards on Page Load
 * Fetch all doctors and display them in the dashboard
 */
async function loadDoctorCards() {
    try {
        // Call getDoctors() to fetch doctor list from backend
        const doctors = await getDoctors();

        // Clear existing content
        const contentDiv = document.getElementById("content");
        if (!contentDiv) {
            console.error("Content div not found");
            return;
        }
        contentDiv.innerHTML = "";

        // Iterate through results and inject them using createDoctorCard()
        if (doctors && doctors.length > 0) {
            doctors.forEach(doctor => {
                const card = createDoctorCard(doctor);
                contentDiv.appendChild(card);
            });
        } else {
            contentDiv.innerHTML = "<p>No doctors found.</p>";
        }
    } catch (error) {
        console.error("Error loading doctor cards:", error);
        const contentDiv = document.getElementById("content");
        if (contentDiv) {
            contentDiv.innerHTML = "<p>Error loading doctors. Please try again later.</p>";
        }
    }
}

/**
 * Setup Search and Filter Event Listeners
 */
function setupFilterListeners() {
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("timeFilter");
    const filterSpecialty = document.getElementById("specialtyFilter");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }

    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }

    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }
}

/**
 * Filter Doctors on Change
 * Gathers current filter/search values and fetches filtered results
 */
async function filterDoctorsOnChange() {
    try {
        // Gather current filter/search values
        const searchBar = document.getElementById("searchBar");
        const filterTime = document.getElementById("timeFilter");
        const filterSpecialty = document.getElementById("specialtyFilter");

        const name = searchBar?.value.trim() || null;
        const time = filterTime?.value || null;
        const specialty = filterSpecialty?.value || null;

        // Fetch and display filtered results using filterDoctors()
        const doctors = await filterDoctors(name, time, specialty);

        // Get content div
        const contentDiv = document.getElementById("content");
        if (!contentDiv) {
            return;
        }

        // Clear existing content
        contentDiv.innerHTML = "";

        // If doctors are found, render them
        if (doctors && doctors.length > 0) {
            renderDoctorCards(doctors);
        } else {
            // If no match, show message "No doctors found"
            contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        }
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("An error occurred while filtering doctors. Please try again.");
    }
}

/**
 * Render Doctor Cards
 * Utility function to render doctor cards when passed a list
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) {
        return;
    }

    // Clear the content area
    contentDiv.innerHTML = "";

    // Loop through the doctors and append each card to the content area
    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Handle Add Doctor Modal
 * Collects form data and adds a new doctor to the system
 */
window.adminAddDoctor = async function () {
    try {
        // Collect input values from the modal form
        const name = document.getElementById('doctorName')?.value.trim();
        const specialty = document.getElementById('specialization')?.value.trim();
        const email = document.getElementById('doctorEmail')?.value.trim();
        const password = document.getElementById('doctorPassword')?.value;
        const phone = document.getElementById('doctorPhone')?.value.trim();

        // Validate required fields
        if (!name || !specialty || !email || !password || !phone) {
            alert("Please fill in all required fields.");
            return;
        }

        // Collect checkbox values for doctor availability
        const availabilityCheckboxes = document.querySelectorAll('input[name="availability"]:checked');
        const availableTimes = Array.from(availabilityCheckboxes).map(cb => cb.value);

        // Build a doctor object with the form values
        const doctor = {
            name: name,
            specialty: specialty,
            email: email,
            password: password,
            phone: phone,
            availableTimes: availableTimes
        };

        // Retrieve the authentication token from localStorage
        const token = localStorage.getItem('token');
        if (!token) {
            alert("Authentication required. Please log in again.");
            window.location.href = "/";
            return;
        }

        // Call saveDoctor(doctor, token) from the service
        const result = await saveDoctor(doctor, token);

        // If save is successful
        if (result.success) {
            // Show a success message
            alert(result.message || "Doctor added successfully!");
            
            // Close the modal
            const modal = document.getElementById('modal');
            if (modal) {
                modal.style.display = 'none';
            }
            
            // Reload the doctor list
            await loadDoctorCards();
        } else {
            // If saving fails, show an error message
            alert(result.message || "Failed to add doctor. Please try again.");
        }
    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("An error occurred while adding the doctor. Please try again.");
    }
};
