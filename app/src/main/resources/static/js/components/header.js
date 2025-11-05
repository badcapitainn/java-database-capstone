// header.js

// Render header based on user role and session
function renderHeader() {
    const headerDiv = document.getElementById("header");
    
    if (!headerDiv) {
        return;
    }

    // Check if the current page is the root page
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="./assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>`;
        return;
    }

    // Retrieve the user's role and token from localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Initialize header content with logo section
    let headerContent = `<header class="header">
        <div class="logo-section">
            <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
            <span class="logo-title">Hospital CMS</span>
        </div>
        <nav>`;

    // Handle session expiry or invalid login
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Add role-specific header content
    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="adminLogout">Logout</a>`;
    } else if (role === "doctor") {
        headerContent += `
            <button id="doctorHome" class="adminBtn">Home</button>
            <a href="#" id="doctorLogout">Logout</a>`;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="home" class="adminBtn">Home</button>
            <button id="patientAppointments" class="adminBtn">Appointments</button>
            <a href="#" id="loggedPatientLogout">Logout</a>`;
    }

    // Close the header section
    headerContent += `</nav></header>`;

    // Render the header content
    headerDiv.innerHTML = headerContent;

    // Attach event listeners to header buttons
    attachHeaderButtonListeners();
}

// Attach event listeners to dynamically created buttons
function attachHeaderButtonListeners() {
    // Admin - Add Doctor Button
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof window.openModal === 'function') {
                window.openModal('addDoctor');
            }
        });
    }

    // Admin - Logout
    const adminLogout = document.getElementById("adminLogout");
    if (adminLogout) {
        adminLogout.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    // Doctor - Home Button
    const doctorHome = document.getElementById("doctorHome");
    if (doctorHome) {
        doctorHome.addEventListener("click", () => {
            if (typeof selectRole === 'function') {
                selectRole('doctor');
            }
        });
    }

    // Doctor - Logout
    const doctorLogout = document.getElementById("doctorLogout");
    if (doctorLogout) {
        doctorLogout.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    // Patient Login Button
    const patientLoginBtn = document.getElementById("patientLogin");
    if (patientLoginBtn) {
        patientLoginBtn.addEventListener("click", () => {
            if (typeof window.openModal === 'function') {
                window.openModal('patientLogin');
            }
        });
    }

    // Patient Signup Button
    const patientSignupBtn = document.getElementById("patientSignup");
    if (patientSignupBtn) {
        patientSignupBtn.addEventListener("click", () => {
            if (typeof window.openModal === 'function') {
                window.openModal('patientSignup');
            }
        });
    }

    // Logged Patient - Home Button
    const homeBtn = document.getElementById("home");
    if (homeBtn) {
        homeBtn.addEventListener("click", () => {
            window.location.href = '/pages/loggedPatientDashboard.html';
        });
    }

    // Logged Patient - Appointments Button
    const patientAppointments = document.getElementById("patientAppointments");
    if (patientAppointments) {
        patientAppointments.addEventListener("click", () => {
            window.location.href = '/pages/patientAppointments.html';
        });
    }

    // Logged Patient - Logout
    const loggedPatientLogout = document.getElementById("loggedPatientLogout");
    if (loggedPatientLogout) {
        loggedPatientLogout.addEventListener("click", (e) => {
            e.preventDefault();
            logoutPatient();
        });
    }
}

// Logout function for admin and doctor
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

// Logout function for patient
function logoutPatient() {
    localStorage.removeItem("token");
    // Set role back to "patient" instead of removing it
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

// Make functions available globally
window.logout = logout;
window.logoutPatient = logoutPatient;

// Call renderHeader when the script loads
renderHeader();
