// footer.js

// Function to render the footer content into the page
function renderFooter() {
    // Access the footer container
    const footer = document.getElementById("footer");
    
    // Check if footer element exists
    if (!footer) {
        return;
    }

    // Use absolute path from root to ensure it works from all page locations
    const logoPath = "/assets/images/logo/logo.png";

    // Inject HTML content
    footer.innerHTML = `
        <footer class="footer">
            <div class="footer-container">
                <div class="footer-logo">
                    <img src="${logoPath}" alt="Hospital CMS Logo">
                    <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
                </div>
                <div class="footer-links">
                    <div class="footer-column">
                        <h4>Company</h4>
                        <a href="#">About</a>
                        <a href="#">Careers</a>
                        <a href="#">Press</a>
                    </div>
                    <div class="footer-column">
                        <h4>Support</h4>
                        <a href="#">Account</a>
                        <a href="#">Help Center</a>
                        <a href="#">Contact Us</a>
                    </div>
                    <div class="footer-column">
                        <h4>Legals</h4>
                        <a href="#">Terms & Conditions</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Licensing</a>
                    </div>
                </div>
            </div>
        </footer>
    `;
}

// Call the renderFooter function to populate the footer in the page
renderFooter();
