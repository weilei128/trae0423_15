package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Examination;
import com.hospital.outpatient.entity.MedicalRecord;
import com.hospital.outpatient.entity.User;
import com.hospital.outpatient.repository.ExaminationRepository;
import com.hospital.outpatient.repository.MedicalRecordRepository;
import com.hospital.outpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ExaminationService {

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Examination createExamination(Examination examination) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(examination.getMedicalRecord().getId())
                .orElseThrow(() -> new RuntimeException("病历不存在"));
        
        examination.setMedicalRecord(medicalRecord);
        examination.setPatient(medicalRecord.getPatient());
        examination.setDoctor(medicalRecord.getDoctor());
        examination.setExamNo(generateExamNo());
        examination.setStatus(Examination.ExamStatus.PENDING);
        
        return examinationRepository.save(examination);
    }

    private String generateExamNo() {
        return "EXAM" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public Examination getExaminationById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("检查申请不存在"));
    }

    public Examination getExaminationByNo(String examNo) {
        return examinationRepository.findByExamNo(examNo)
                .orElseThrow(() -> new RuntimeException("检查申请不存在"));
    }

    public List<Examination> getPatientExaminations(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        return examinationRepository.findByPatientOrderByCreateTimeDesc(patient);
    }

    public List<Examination> getExaminationsByMedicalRecord(Long medicalRecordId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                .orElseThrow(() -> new RuntimeException("病历不存在"));
        return examinationRepository.findByMedicalRecord(medicalRecord);
    }

    @Transactional
    public Examination updateExaminationStatus(Long id, Examination.ExamStatus status) {
        Examination examination = getExaminationById(id);
        examination.setStatus(status);
        examination.setUpdateTime(LocalDateTime.now());
        
        if (status == Examination.ExamStatus.IN_PROGRESS) {
            examination.setExamTime(LocalDateTime.now());
        } else if (status == Examination.ExamStatus.REPORTED) {
            examination.setReportTime(LocalDateTime.now());
        }
        
        return examinationRepository.save(examination);
    }

    @Transactional
    public Examination updateExamResult(Long id, String examResult, String reportUrl) {
        Examination examination = getExaminationById(id);
        examination.setExamResult(examResult);
        examination.setReportUrl(reportUrl);
        examination.setStatus(Examination.ExamStatus.REPORTED);
        examination.setReportTime(LocalDateTime.now());
        examination.setUpdateTime(LocalDateTime.now());
        
        return examinationRepository.save(examination);
    }
}
