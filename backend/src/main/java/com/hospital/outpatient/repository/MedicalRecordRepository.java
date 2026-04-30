package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.MedicalRecord;
import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByCreateTimeDesc(User patient);
    Optional<MedicalRecord> findByAppointment(Appointment appointment);
}
