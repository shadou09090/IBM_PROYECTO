-- Smart Clinic Management System (Hospital CMS) - Stored Procedures
USE cms;

DELIMITER $$

DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor $$
CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty AS doctor_specialty,
        d.email AS doctor_email,
        d.phone AS doctor_phone,
        a.id AS appointment_id,
        a.appointment_time,
        CASE a.status
            WHEN 0 THEN 'Scheduled'
            WHEN 1 THEN 'Completed'
            WHEN 2 THEN 'Cancelled'
            ELSE 'Unknown'
        END AS appointment_status,
        p.id AS patient_id,
        p.name AS patient_name,
        p.email AS patient_email,
        p.phone AS patient_phone
    FROM appointment a
    INNER JOIN doctor d ON a.doctor_id = d.id
    INNER JOIN patient p ON a.patient_id = p.id
    WHERE DATE(a.appointment_time) = report_date
    ORDER BY d.name ASC, a.appointment_time ASC;
END $$

DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth $$
CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN input_month INT, IN input_year INT)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty AS doctor_specialty,
        COUNT(DISTINCT a.patient_id) AS total_patients,
        COUNT(a.id) AS total_appointments
    FROM appointment a
    INNER JOIN doctor d ON a.doctor_id = d.id
    WHERE MONTH(a.appointment_time) = input_month
      AND YEAR(a.appointment_time) = input_year
      AND a.status <> 2
    GROUP BY d.id, d.name, d.specialty
    ORDER BY total_patients DESC, total_appointments DESC
    LIMIT 1;
END $$

DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear $$
CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN input_year INT)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty AS doctor_specialty,
        COUNT(DISTINCT a.patient_id) AS total_patients,
        COUNT(a.id) AS total_appointments
    FROM appointment a
    INNER JOIN doctor d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time) = input_year
      AND a.status <> 2
    GROUP BY d.id, d.name, d.specialty
    ORDER BY total_patients DESC, total_appointments DESC
    LIMIT 1;
END $$

DELIMITER ;
