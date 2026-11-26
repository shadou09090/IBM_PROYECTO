# User Stories & Role-Based Permissions — Hospital CMS

## Role Matrix Overview
| Capability | Admin | Doctor | Patient |
| :--- | :---: | :---: | :---: |
| Login & receive JWT | Yes | Yes | Yes |
| View/Search/Filter Doctors | Yes | No | Yes |
| Add/Delete Doctors | Yes | No | No |
| View Own Appointments | No | Yes | Yes |
| Filter Appointments by Date / Patient | No | Yes | No |
| Book / Update / Cancel Appointment | No | No | Yes |
| View Patient Previous Prescriptions | No | Yes | Yes (own) |
| Create Prescription for Appointment | No | Yes | No |

---

## 1. Admin User Stories
- **US-A1 (Authentication)**: As an Admin, I want to log in with my username and password so that I receive a JWT and access `/adminDashboard/{token}`.
- **US-A2 (Doctor Directory)**: As an Admin, I want to view all registered doctors with their specialty, contact info, and available time slots so that I can manage clinic staffing.
- **US-A3 (Search & Filter Doctors)**: As an Admin, I want to search doctors by name and filter by specialty or availability (`AM` / `PM`) so that I can quickly locate staff.
- **US-A4 (Add Doctor)**: As an Admin, I want to add a new doctor with name, specialty, email, phone, password, and available time slots so that patients can book appointments with them.
- **US-A5 (Delete Doctor)**: As an Admin, I want to remove a doctor from the system so that inactive practitioners no longer appear for booking.
- **Restrictions**: Admins cannot create prescriptions or book/modify patient appointments directly.

## 2. Doctor User Stories
- **US-D1 (Authentication)**: As a Doctor, I want to log in with my email and password so that I can access `/doctorDashboard/{token}`.
- **US-D2 (Appointment Schedule)**: As a Doctor, I want to view my scheduled appointments with patient details, date/time, and status so that I can prepare for consultations.
- **US-D3 (Filter & Search Appointments)**: As a Doctor, I want to filter my appointments by date and search by patient name so that I can find specific visits.
- **US-D4 (Patient Record / Previous Prescriptions)**: As a Doctor, I want to view a patient's previous prescriptions so that I have clinical context before prescribing new medication.
- **US-D5 (Add Prescription)**: As a Doctor, I want to create a prescription (medication, dosage, doctor notes) linked to an appointment so that the patient has a permanent digital record.
- **Restrictions**: Doctors can only view their own appointments and cannot add or delete other doctors.

## 3. Patient User Stories
- **US-P1 (Registration & Login)**: As a Patient, I want to register an account or log in with my email and password so that I can manage my healthcare online.
- **US-P2 (Browse & Filter Doctors)**: As a Patient, I want to search doctors by name, specialty, and `AM`/`PM` availability so that I can choose the right provider.
- **US-P3 (Book Appointment)**: As a Patient, I want to book a 1-hour appointment slot with a doctor, with automatic conflict checking so that double-booking is prevented.
- **US-P4 (Update / Cancel Appointment)**: As a Patient, I want to reschedule or cancel my upcoming appointment so that my schedule stays accurate.
- **US-P5 (View Patient Record & Prescriptions)**: As a Patient, I want to view my appointment history (`Date`, `Appointment ID`, `Patient ID`, `Prescription`) and inspect my prescriptions so that I can follow treatment instructions.
- **Restrictions**: Patients can only view, update, or cancel their own appointments and prescriptions, and cannot access Admin or Doctor dashboards.
