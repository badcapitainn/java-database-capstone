// doctorDashboard.js

// Import required modules
import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Initialize Global Variables
const tableBody = document.getElementById("patientTableBody");

// Initialize selectedDate to today's date in 'YYYY-MM-DD' format
const today = new Date();
const todayString = today.toISOString().split('T')[0];
let selectedDate = todayString;

// Get token from localStorage (used for authentication)
const token = localStorage.getItem('token');

// Initialize patientName as null, for search filtering
let patientName = null;

// Setup Search Bar Functionality
document.addEventListener('DOMContentLoaded', () => {
    const searchBar = document.getElementById("searchBar");
    
    if (searchBar) {
        searchBar.addEventListener("input", (e) => {
            // Update the patientName variable
            const value = e.target.value.trim();
            
            // If the search input is empty, default patientName to "null"
            patientName = value.length > 0 ? value : "null";
            
            // Call loadAppointments() to refresh the list with the filtered data
            loadAppointments();
        });
    }

    // Bind Event Listeners to Filter Controls
    setupFilterControls();

    // Initial Render on Page Load
    if (typeof renderContent === 'function') {
        renderContent();
    }
    
    // Call loadAppointments() to load today's appointments by default
    loadAppointments();
});

/**
 * Setup Filter Controls
 * Binds event listeners to "Today's Appointments" button and date picker
 */
function setupFilterControls() {
    // "Today's Appointments" button
    const todayButton = document.getElementById("todayButton");
    if (todayButton) {
        todayButton.addEventListener("click", () => {
            // Reset the selectedDate to today
            selectedDate = todayString;
            
            // Update the date picker field to reflect today's date
            const datePicker = document.getElementById("datePicker");
            if (datePicker) {
                datePicker.value = todayString;
            }
            
            // Call loadAppointments()
            loadAppointments();
        });
    }

    // Date picker
    const datePicker = document.getElementById("datePicker");
    if (datePicker) {
        // Set initial value to today
        datePicker.value = todayString;
        
        datePicker.addEventListener("change", (e) => {
            // Update the selectedDate variable when changed
            selectedDate = e.target.value;
            
            // Call loadAppointments() to fetch and display appointments for the selected date
            loadAppointments();
        });
    }
}

/**
 * Load Appointments Function
 * Fetch and display appointments based on selected date and optional patient name
 */
async function loadAppointments() {
    if (!tableBody) {
        console.error("Table body not found");
        return;
    }

    // Validate token exists
    if (!token) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: red;">
                    Authentication required. Please log in again.
                </td>
            </tr>
        `;
        return;
    }

    try {
        // Step 1: Call getAllAppointments with selectedDate, patientName, and token
        const data = await getAllAppointments(selectedDate, patientName || "null", token);

        // Step 2: Clear the table body content before rendering new rows
        tableBody.innerHTML = "";

        // Step 3: If no appointments are returned
        if (!data || !data.appointments || data.appointments.length === 0) {
            const noAppointmentsRow = document.createElement("tr");
            noAppointmentsRow.innerHTML = `
                <td colspan="5" style="text-align: center; font-style: italic; color: #666;">
                    No Appointments found for ${selectedDate === todayString ? 'today' : selectedDate}.
                </td>
            `;
            tableBody.appendChild(noAppointmentsRow);
            return;
        }

        // Step 4: If appointments exist
        const appointments = data.appointments || [];
        
        appointments.forEach(appointment => {
            // Extract the patient's details from the appointment
            const patient = {
                id: appointment.patient?.id || appointment.patientId || 'N/A',
                name: appointment.patient?.name || 'N/A',
                phone: appointment.patient?.phone || 'N/A',
                email: appointment.patient?.email || 'N/A'
            };

            // Get doctor ID from appointment
            const doctorId = appointment.doctor?.id || appointment.doctorId || null;
            const appointmentId = appointment.id || appointment.appointmentId || null;

            // Call createPatientRow() to create a <tr> for each
            const row = createPatientRow(patient, appointmentId, doctorId);
            
            // Append each row to the appointment table body
            tableBody.appendChild(row);
        });
    } catch (error) {
        // Step 5: Catch and handle any errors during fetch
        console.error("Error loading appointments:", error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: red;">
                    Error loading appointments. Try again later.
                </td>
            </tr>
        `;
    }
}
