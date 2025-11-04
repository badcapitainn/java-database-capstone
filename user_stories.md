# User Story Template
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._
**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]
**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

# Admin User Stories

## Story 1: Admin Login
**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can enter username and password on a dedicated login page
2. System validates credentials against the admin user database
3. Upon successful authentication, admin is redirected to the admin dashboard
4. Failed login attempts display an appropriate error message
5. Session is created and maintained for authenticated admin users

**Priority:** High

**Story Points:** 5

**Notes:**
- Implement secure password hashing (bcrypt or similar)
- Consider adding rate limiting to prevent brute force attacks
- May need to implement "forgot password" functionality in future
- Ensure HTTPS is enforced for all login requests

---

## Story 2: Admin Logout
**Title:**
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Logout button/link is visible on all admin portal pages
2. Clicking logout terminates the admin's session
3. Admin is redirected to the login page after logout
4. System prevents access to admin pages after logout without re-authentication
5. Confirmation message is displayed after successful logout

**Priority:** High

**Story Points:** 2

**Notes:**
- Clear all session data and authentication tokens
- Consider implementing session timeout for inactive users
- Ensure logout works consistently across all browsers

---

## Story 3: Add Doctors
**Title:**
_As an admin, I want to add doctors to the portal, so that they can provide services on the platform._

**Acceptance Criteria:**
1. Admin can access an "Add Doctor" form from the admin dashboard
2. Form includes required fields: name, email, specialization, phone number, and credentials
3. System validates all input fields before submission
4. Upon successful submission, doctor profile is created in the database
5. Admin receives confirmation message with the newly created doctor's details
6. New doctor appears in the doctors list immediately

**Priority:** High

**Story Points:** 8

**Notes:**
- Email should be unique and validated for proper format
- Consider adding optional fields like bio, photo, consultation fees
- May need to send welcome email to new doctors with login credentials
- Implement proper error handling for duplicate entries

---

## Story 4: Delete Doctor Profile
**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that I can remove inactive or unauthorized practitioners._

**Acceptance Criteria:**
1. Admin can view a list of all doctors with a delete option for each
2. Clicking delete triggers a confirmation dialog to prevent accidental deletion
3. Upon confirmation, doctor's profile is removed from the database
4. System displays success message after deletion
5. Deleted doctor no longer appears in the doctors list
6. System handles associated data appropriately (appointments, reviews, etc.)

**Priority:** Medium

**Story Points:** 5

**Notes:**
- Consider soft delete vs hard delete for data retention
- Handle edge cases: doctors with upcoming appointments, historical records
- May need to notify the doctor via email about profile removal
- Implement proper authorization checks to ensure only admins can delete
- Consider archiving doctor data instead of permanent deletion for audit purposes

---

## Story 5: Run Monthly Appointments Report
**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. MySQL stored procedure is created and documented with clear naming convention
2. Procedure accepts optional parameters for date range filtering
3. Procedure returns month-wise appointment count in a readable format
4. Results include month/year and total appointment count
5. Admin documentation includes instructions for executing the procedure from MySQL CLI

**Priority:** Low

**Story Points:** 3

**Notes:**

# Patient User Stories

## Story 1: View Doctors List (Public Access)
**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Public-facing page displays a list of all available doctors
2. Each doctor listing shows basic information: name, specialization, and photo (if available)
3. Page is accessible without authentication
4. List can be filtered by specialization or search by doctor name
5. Doctor profiles display additional details like experience and consultation fees
6. Page loads within 3 seconds with responsive design for mobile devices

**Priority:** High

**Story Points:** 5

**Notes:**
- This is a key feature for user acquisition - should be optimized for SEO
- Consider adding doctor ratings/reviews if available
- May include pagination if doctor list is extensive
- Ensure sensitive information (contact details, personal info) is not exposed
- Consider adding a "Book Appointment" CTA that prompts login/signup

---

## Story 2: Patient Sign Up
**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Sign-up form is accessible from the homepage and login page
2. Form includes required fields: full name, email, password, and phone number
3. Email validation ensures proper format and uniqueness
4. Password must meet security requirements (minimum 8 characters, mix of letters and numbers)
5. System creates patient account in database upon successful submission
6. Confirmation email is sent to the registered email address
7. Patient is automatically logged in after successful registration

**Priority:** High

**Story Points:** 8

**Notes:**
- Implement email verification to confirm account ownership
- Add password strength indicator on the form
- Include terms of service and privacy policy acceptance checkbox
- Consider social login options (Google, Facebook) for future enhancement
- Implement CAPTCHA to prevent bot registrations
- Display clear error messages for validation failures

---

## Story 3: Patient Login
**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Login form is accessible from the homepage with email and password fields
2. System validates credentials against the patient database
3. Upon successful authentication, patient is redirected to their dashboard
4. Failed login attempts display appropriate error messages
5. "Remember me" option to maintain session across browser sessions
6. "Forgot password" link is available for password recovery

**Priority:** High

**Story Points:** 5

**Notes:**
- Implement secure password hashing and comparison
- Add rate limiting to prevent brute force attacks (max 5 attempts per 15 minutes)
- Ensure HTTPS is enforced for all login requests
- Session timeout after 30 minutes of inactivity
- Consider implementing 2FA for future security enhancement

---

## Story 4: Patient Logout
**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Logout button is visible in the navigation menu on all patient portal pages
2. Clicking logout terminates the patient's session
3. Patient is redirected to the homepage or login page after logout
4. System prevents access to patient pages after logout without re-authentication
5. Success message confirms logout action

**Priority:** High

**Story Points:** 2

**Notes:**
- Clear all session data, cookies, and authentication tokens
- Ensure logout works across all browsers and devices
- Consider automatic logout after extended inactivity (30 minutes)
- Provide option to "log out from all devices" for security

---

## Story 5: Book Appointment
**Title:**
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Authenticated patients can access the appointment booking page
2. Patient can select a doctor from a dropdown or search list
3. Calendar widget displays available time slots for the selected doctor
4. Each time slot represents a 1-hour appointment window
5. Patient can select date and time slot and add optional notes
6. System validates availability before confirming the booking
7. Confirmation page displays appointment details (doctor, date, time, duration)
8. Confirmation email is sent to patient with appointment details
9. Booked slot becomes unavailable for other patients

**Priority:** High

**Story Points:** 13

**Notes:**
- Appointment duration is fixed at 1 hour as per requirements
- Implement double-booking prevention with database locks
- Only show available slots (exclude already booked times and doctor's unavailable hours)
- Consider adding appointment reminders (email/SMS) 24 hours before
- Handle timezone considerations if applicable
- Add cancellation policy information during booking
- Future enhancement: Online payment integration

---

## Story 6: View Upcoming Appointments
**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patient dashboard displays a list of all upcoming appointments
2. Each appointment shows: doctor name, specialization, date, time, and status
3. Appointments are sorted chronologically (nearest first)
4. Past appointments are not shown in this view or are clearly separated
5. Each appointment has options to view details, reschedule, or cancel
6. Empty state message is displayed when no upcoming appointments exist
7. Page updates in real-time if appointments are modified

**Priority:** High

**Story Points:** 5

**Notes:**
- Display appointments for the next 90 days
- Include appointment status indicators (confirmed, pending, cancelled)
- Add filtering options: by doctor, by date range
- Consider adding calendar view as alternative to list view
- Highlight appointments happening within 24 hours
- Add "Add to Calendar" functionality (iCal, Google Calendar)
- Future enhancement: Include past appointments in a separate "History" tab
- Stored procedure name suggestion: `sp_get_monthly_appointments` or `get_appointments_per_month`
- Consider adding parameters for filtering by doctor, status, or appointment type
- Future enhancement: Create a dashboard UI to display this data without CLI access
- Ensure procedure is optimized for performance with proper indexes
- Document the exact CLI command for non-technical admins: `CALL sp_get_monthly_appointments();`

- # Doctor User Stories

## Story 1: Doctor Login
**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor can access login page with dedicated doctor login option
2. Doctor enters email/username and password for authentication
3. System validates credentials against the doctor database
4. Upon successful authentication, doctor is redirected to their dashboard
5. Failed login attempts display appropriate error messages
6. Session is created and maintained for authenticated doctors

**Priority:** High

**Story Points:** 5

**Notes:**
- Implement secure password hashing (bcrypt or similar)
- Consider separate login URL or portal section for doctors vs patients/admins
- Add rate limiting to prevent brute force attacks
- First-time login may require password change if admin-created account
- Ensure HTTPS is enforced for all login requests
- Consider implementing 2FA for enhanced security given sensitive patient data access

---

## Story 2: Doctor Logout
**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. Logout button is visible in the navigation menu on all doctor portal pages
2. Clicking logout terminates the doctor's session immediately
3. Doctor is redirected to the login page after logout
4. System prevents access to doctor pages after logout without re-authentication
5. All session data and authentication tokens are cleared
6. Confirmation message is displayed after successful logout

**Priority:** High

**Story Points:** 2

**Notes:**
- Implement automatic logout after 15-20 minutes of inactivity (shorter than patient timeout due to sensitive data)
- Clear all cached patient data upon logout
- Ensure logout works consistently across all browsers and devices
- Consider "log out from all devices" option for security
- Important for compliance with patient data protection regulations (HIPAA, GDPR)

---

## Story 3: View Appointment Calendar
**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Doctor dashboard displays a calendar view of all appointments
2. Calendar shows daily, weekly, and monthly view options
3. Each appointment displays patient name, time, and appointment status
4. Appointments are color-coded by status (confirmed, completed, cancelled)
5. Doctor can click on appointment to view full details
6. Calendar clearly shows unavailable time slots marked differently
7. Current date/time is highlighted on the calendar

**Priority:** High

**Story Points:** 8

**Notes:**
- Default to weekly view for better appointment overview
- Include time blocks for lunch/breaks if implemented
- Show appointment count per day for quick overview
- Consider printing functionality for offline reference
- Sync with unavailability markings to show blocked time slots
- Add filter options: by status, by date range
- Future enhancement: Export to Google Calendar, Outlook, or iCal format
- Ensure calendar loads quickly even with many appointments

---

## Story 4: Mark Unavailability
**Title:**
_As a doctor, I want to mark my unavailability, so that I can inform patients of only the available slots._

**Acceptance Criteria:**
1. Doctor can access "Manage Availability" section from their dashboard
2. Doctor can select specific dates and time ranges to mark as unavailable
3. System provides options for recurring unavailability (e.g., every Tuesday 2-4 PM)
4. Unavailable slots are immediately blocked from patient booking system
5. Doctor can view list of all marked unavailability periods
6. Doctor can edit or remove previously marked unavailability
7. System prevents marking times as unavailable if appointments already exist
8. Confirmation message is displayed after successfully marking unavailability

**Priority:** High

**Story Points:** 10

**Notes:**
- Implement validation to prevent blocking time slots with existing appointments
- Provide warning if trying to mark unavailability close to existing appointments
- Consider categories: vacation, personal time, medical leave, training
- Add bulk unavailability marking for holidays or extended leave
- Consider requiring admin approval for extended unavailability periods
- Send notifications to patients if appointments need rescheduling due to unavailability
- Future enhancement: Integrate with hospital/clinic schedule systems
- Include option to add notes for unavailability (visible only to admin/doctor)

---

## Story 5: Update Doctor Profile
**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Doctor can access "Edit Profile" page from their dashboard
2. Profile form includes fields: name, specialization, phone number, email, bio, consultation fees, and experience
3. Doctor can upload or update profile photo
4. All changes are validated before submission (email format, phone number format)
5. Upon successful update, changes are reflected immediately on the public doctor listing
6. Doctor receives confirmation message after successful profile update
7. Email changes require verification via confirmation link

**Priority:** Medium

**Story Points:** 8

**Notes:**
- Email should remain unique across all doctor accounts
- Consider admin approval for certain fields like specialization or credentials
- Profile photo should have size and format restrictions (max 2MB, JPG/PNG)
- Include preview functionality to see how profile appears to patients
- Some fields may be admin-only (e.g., credentials, license numbers)
- Add validation for required fields vs optional fields
- Consider version history for profile changes (audit trail)
- Future enhancement: Add languages spoken, medical school, certifications

---

## Story 6: View Patient Details for Upcoming Appointments
**Title:**
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Doctor can access a list of upcoming appointments from their dashboard
2. Each appointment entry displays patient name, appointment date/time, and contact information
3. Doctor can click on an appointment to view full patient details
4. Patient details page shows: full name, age, phone number, email, and appointment notes
5. Appointments are sorted chronologically (nearest first)
6. Search and filter functionality available (by patient name, date range)
7. Patient medical history or previous appointment notes are accessible if available
8. Export option available for appointment list (PDF or CSV)

**Priority:** High

**Story Points:** 8

**Notes:**
- Display only upcoming appointments (future dates) by default
- Highlight appointments happening within next 24 hours
- Include patient notes or reason for visit if provided during booking
- Ensure patient data is displayed securely and in compliance with privacy regulations
- Consider adding quick actions: call patient, send message, reschedule
- Show appointment status clearly (confirmed, pending, completed, cancelled)
- Add option to view past appointments with the same patient for continuity of care
- Future enhancement: Integration with electronic health records (EHR) system
- Consider adding notes field for doctor to add pre-appointment observations
