// patientDashboard.js

// Import required modules
import { createDoctorCard } from './components/doctorCard.js';
import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { patientLogin, patientSignup } from './services/patientServices.js';

// Make openModal available globally
window.openModal = openModal;

// Load Doctor Cards on Page Load
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
    
    // Bind Modal Triggers for Login and Signup
    bindModalTriggers();
    
    // Setup Search and Filter Logic
    setupSearchAndFilter();
});

/**
 * Load Doctor Cards
 * Calls getDoctors() to fetch the list of all available doctors and renders them
 */
async function loadDoctorCards() {
    try {
        // Call getDoctors() to fetch the list of all available doctors
        const doctors = await getDoctors();

        // Clear any existing content inside the #content div
        const contentDiv = document.getElementById("content");
        if (!contentDiv) {
            console.error("Content div not found");
            return;
        }
        contentDiv.innerHTML = "";

        // Iterate over the results and render each doctor using createDoctorCard()
        if (doctors && doctors.length > 0) {
            doctors.forEach(doctor => {
                const card = createDoctorCard(doctor);
                contentDiv.appendChild(card);
            });
        } else {
            contentDiv.innerHTML = "<p>No doctors available at the moment.</p>";
        }
    } catch (error) {
        console.error("Failed to load doctors:", error);
        const contentDiv = document.getElementById("content");
        if (contentDiv) {
            contentDiv.innerHTML = "<p>Error loading doctors. Please try again later.</p>";
        }
    }
}

/**
 * Bind Modal Triggers for Login and Signup
 */
function bindModalTriggers() {
    // The Signup button (#patientSignup) → opens the patientSignup modal
    const signupBtn = document.getElementById("patientSignup");
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }

    // The Login button (#patientLogin) → opens the patientLogin modal
    const loginBtn = document.getElementById("patientLogin");
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }
}

/**
 * Setup Search and Filter Logic
 * Set up listeners for search bar and filter dropdowns
 */
function setupSearchAndFilter() {
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

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
 * Gathers values from all three filter/search inputs and fetches filtered results
 */
async function filterDoctorsOnChange() {
    try {
        // Gather values from all three filter/search inputs
        const searchBar = document.getElementById("searchBar");
        const filterTime = document.getElementById("filterTime");
        const filterSpecialty = document.getElementById("filterSpecialty");

        const name = searchBar?.value.trim() || null;
        const time = filterTime?.value || null;
        const specialty = filterSpecialty?.value || null;

        // Use filterDoctors(name, time, specialty) to fetch filtered results
        const doctors = await filterDoctors(name, time, specialty);

        // Get content div
        const contentDiv = document.getElementById("content");
        if (!contentDiv) {
            return;
        }

        // Clear the existing content
        contentDiv.innerHTML = "";

        // If doctors are found, renders them using createDoctorCard()
        if (doctors && doctors.length > 0) {
            doctors.forEach(doctor => {
                const card = createDoctorCard(doctor);
                contentDiv.appendChild(card);
            });
        } else {
            // If not, displays a fallback message
            contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        }
    } catch (error) {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
        const contentDiv = document.getElementById("content");
        if (contentDiv) {
            contentDiv.innerHTML = "<p>Error filtering doctors. Please try again.</p>";
        }
    }
}

/**
 * Render Utility
 * Function to render a given list of doctors dynamically
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) {
        return;
    }

    contentDiv.innerHTML = "";

    if (doctors && doctors.length > 0) {
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } else {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
    }
}

/**
 * Handle Patient Signup
 * The signupPatient() function is triggered on form submission
 */
window.signupPatient = async function () {
    try {
        // Collect user inputs (name, email, password, phone, address)
        const name = document.getElementById("name")?.value.trim();
        const email = document.getElementById("email")?.value.trim();
        const password = document.getElementById("password")?.value;
        const phone = document.getElementById("phone")?.value.trim();
        const address = document.getElementById("address")?.value.trim();

        // Validate required fields
        if (!name || !email || !password || !phone || !address) {
            alert("Please fill in all required fields.");
            return;
        }

        // Create data object
        const data = { name, email, password, phone, address };

        // Send the data to the backend via patientSignup()
        const result = await patientSignup(data);

        // On success
        if (result.success) {
            // Show an alert with a success message
            alert(result.message || "Signup successful! Please log in.");
            
            // Close the modal
            const modal = document.getElementById("modal");
            if (modal) {
                modal.style.display = "none";
            }
            
            // Reload the page
            window.location.reload();
        } else {
            // On failure: Show an error message
            alert(result.message || "Signup failed. Please try again.");
        }
    } catch (error) {
        console.error("Signup failed:", error);
        alert("❌ An error occurred while signing up.");
    }
};

/**
 * Handle Patient Login
 * The loginPatient() function is triggered on login form submission
 */
window.loginPatient = async function () {
    try {
        // Capture login credentials (email, password)
        const email = document.getElementById("email")?.value.trim();
        const password = document.getElementById("password")?.value;

        // Validate inputs
        if (!email || !password) {
            alert("Please enter both email and password.");
            return;
        }

        const data = { email, password };

        // Call patientLogin() to authenticate
        const response = await patientLogin(data);

        // On success
        if (response.ok) {
            // Parse the response to get the token
            const result = await response.json();
            
            // Store JWT token in localStorage
            if (result.token) {
                localStorage.setItem('token', result.token);
                
                // Set role to loggedPatient
                if (typeof setRole === 'function') {
                    setRole('loggedPatient');
                } else {
                    localStorage.setItem('userRole', 'loggedPatient');
                }
                
                // Redirect user to loggedPatientDashboard.html
                window.location.href = '/pages/loggedPatientDashboard.html';
            } else {
                alert("Login successful but no token received. Please try again.");
            }
        } else {
            // On failure: Show error alert
            const errorData = await response.json().catch(() => ({ message: 'Invalid credentials!' }));
            alert(errorData.message || '❌ Invalid credentials!');
        }
    } catch (error) {
        console.error("Error during patient login:", error);
        alert("❌ Failed to Login. Please try again.");
    }
};
