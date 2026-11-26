package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    List<Doctor> findByNameContainingIgnoreCase(String name);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.availableTimes t "
            + "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "AND LOWER(t) LIKE LOWER(CONCAT('%', :period, '%'))")
    List<Doctor> findByNameAndTimePeriod(@Param("name") String name, @Param("period") String period);
}
