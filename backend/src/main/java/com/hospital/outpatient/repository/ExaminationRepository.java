package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.Examination;
import com.hospital.outpatient.entity.MedicalRecord;
import com.hospital.outpatient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findByPatientOrderByCreateTimeDesc(User patient);
    List<Examination> findByMedicalRecord(MedicalRecord medicalRecord);
    Optional<Examination> findByExamNo(String examNo);
}
