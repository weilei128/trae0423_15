package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.Department;
import com.hospital.outpatient.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByDepartment(Department department);
    List<Doctor> findByAvailableTrue();
    Optional<Doctor> findByUserId(Long userId);
}
