// index.js

// Import required modules
import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

// Define API endpoints
const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// Make openModal available globally for onclick handlers
window.openModal = openModal;

// Setup button event listeners
window.onload = function () {
    // Select Admin login button
    const adminBtn = document.getElementById('adminLogin');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    // Also handle the role selection button on index page
    const adminRoleBtn = document.getElementById('adminBtn');
    if (adminRoleBtn) {
        adminRoleBtn.addEventListener('click', () => {
            // Store role first, then open login modal
            if (typeof setRole === 'function') {
                setRole('admin');
            }
            openModal('adminLogin');
        });
    }

    // Select Doctor login button
    const doctorBtn = document.getElementById('doctorLogin');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }

    // Also handle the role selection button on index page
    const doctorRoleBtn = document.getElementById('doctorBtn');
    if (doctorRoleBtn) {
        doctorRoleBtn.addEventListener('click', () => {
            // Store role first, then open login modal
            if (typeof setRole === 'function') {
                setRole('doctor');
            }
            openModal('doctorLogin');
        });
    }
};

// Implement Admin Login Handler
window.adminLoginHandler = async function () {
    try {
        // Step 1: Get the entered username and password from the input fields
        const username = document.getElementById('username')?.value;
        const password = document.getElementById('password')?.value;

        // Validate inputs
        if (!username || !password) {
            alert("Please enter both username and password.");
            return;
        }

        // Step 2: Create an admin object with these credentials
        const admin = { username, password };

        // Step 3: Use fetch() to send a POST request to the Admin login API
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(admin)
        });

        // Step 4: Handle the response
        if (response.ok) {
            // Extract the response JSON
            const data = await response.json();
            
            // Store the received token in localStorage
            if (data.token) {
                localStorage.setItem('token', data.token);
                
                // Call selectRole('admin') to proceed with admin-specific behavior
                if (typeof selectRole === 'function') {
                    selectRole('admin');
                } else {
                    // Fallback: manually set role and redirect
                    if (typeof setRole === 'function') {
                        setRole('admin');
                    }
                    window.location.href = `/adminDashboard/${data.token}`;
                }
            } else {
                alert("Login successful but no token received. Please try again.");
            }
        } else {
            // Step 5: If login fails, display an alert
            const errorData = await response.json().catch(() => ({ message: 'Invalid credentials!' }));
            alert(errorData.message || "Invalid credentials!");
        }
    } catch (error) {
        // Step 6: Handle unexpected errors
        console.error("Error during admin login:", error);
        alert("An error occurred. Please try again later.");
    }
};

// Implement Doctor Login Handler
window.doctorLoginHandler = async function () {
    try {
        // Step 1: Get the entered email and password from the input fields
        const email = document.getElementById('email')?.value;
        const password = document.getElementById('password')?.value;

        // Validate inputs
        if (!email || !password) {
            alert("Please enter both email and password.");
            return;
        }

        // Step 2: Create a doctor object with these credentials
        const doctor = { email, password };

        // Step 3: Use fetch() to send a POST request to the Doctor login endpoint
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(doctor)
        });

        // Step 4: Handle the response
        if (response.ok) {
            // Parse the JSON response to get the token
            const data = await response.json();
            
            // Store the received token in localStorage
            if (data.token) {
                localStorage.setItem('token', data.token);
                
                // Call selectRole('doctor') to proceed with doctor-specific behavior
                if (typeof selectRole === 'function') {
                    selectRole('doctor');
                } else {
                    // Fallback: manually set role and redirect
                    if (typeof setRole === 'function') {
                        setRole('doctor');
                    }
                    window.location.href = `/doctorDashboard/${data.token}`;
                }
            } else {
                alert("Login successful but no token received. Please try again.");
            }
        } else {
            // Step 5: If login fails, show an alert
            const errorData = await response.json().catch(() => ({ message: 'Invalid credentials!' }));
            alert(errorData.message || "Invalid credentials!");
        }
    } catch (error) {
        // Step 6: Handle unexpected errors
        console.error("Error during doctor login:", error);
        alert("An error occurred. Please try again later.");
    }
};
