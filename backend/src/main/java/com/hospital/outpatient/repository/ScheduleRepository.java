package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.Schedule;
import com.hospital.outpatient.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDoctorAndScheduleDate(Doctor doctor, LocalDate scheduleDate);
    List<Schedule> findByDepartmentAndScheduleDate(Department department, LocalDate scheduleDate);
    List<Schedule> findByScheduleDate(LocalDate scheduleDate);
    List<Schedule> findByDoctor(Doctor doctor);
}
