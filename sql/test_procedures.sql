-- Test script for Hospital CMS MySQL Stored Procedures
USE cms;

-- 1. Daily appointment report for a specific date
CALL GetDailyAppointmentReportByDoctor('2026-03-10');
CALL GetDailyAppointmentReportByDoctor('2026-11-26');

-- 2. Doctor with most patients in a specific month and year
CALL GetDoctorWithMostPatientsByMonth(10, 2025);
CALL GetDoctorWithMostPatientsByMonth(3, 2026);

-- 3. Doctor with most patients in a specific year
CALL GetDoctorWithMostPatientsByYear(2025);
CALL GetDoctorWithMostPatientsByYear(2026);
