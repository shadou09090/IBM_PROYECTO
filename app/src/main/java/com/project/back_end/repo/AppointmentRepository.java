package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientIdAndStatus(Long patientId, int status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId "
            + "AND a.appointmentTime BETWEEN :start AND :end "
            + "AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    List<Appointment> findByDoctorIdAndDateRangeAndPatientName(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("patientName") String patientName);
}
