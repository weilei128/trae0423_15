package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.MedicalRecord;
import com.hospital.outpatient.entity.User;
import com.hospital.outpatient.repository.AppointmentRepository;
import com.hospital.outpatient.repository.MedicalRecordRepository;
import com.hospital.outpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        Appointment appointment = appointmentRepository.findById(medicalRecord.getAppointment().getId())
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        medicalRecord.setAppointment(appointment);
        medicalRecord.setPatient(appointment.getPatient());
        medicalRecord.setDoctor(appointment.getDoctor());
        
        return medicalRecordRepository.save(medicalRecord);
    }

    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("病历不存在"));
    }

    public MedicalRecord getMedicalRecordByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        return medicalRecordRepository.findByAppointment(appointment)
                .orElseThrow(() -> new RuntimeException("病历不存在"));
    }

    public List<MedicalRecord> getPatientMedicalRecords(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        return medicalRecordRepository.findByPatientOrderByCreateTimeDesc(patient);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord medicalRecordDetails) {
        MedicalRecord medicalRecord = getMedicalRecordById(id);
        
        medicalRecord.setChiefComplaint(medicalRecordDetails.getChiefComplaint());
        medicalRecord.setPresentIllness(medicalRecordDetails.getPresentIllness());
        medicalRecord.setPastHistory(medicalRecordDetails.getPastHistory());
        medicalRecord.setPhysicalExamination(medicalRecordDetails.getPhysicalExamination());
        medicalRecord.setDiagnosis(medicalRecordDetails.getDiagnosis());
        medicalRecord.setTreatmentPlan(medicalRecordDetails.getTreatmentPlan());
        medicalRecord.setPrescription(medicalRecordDetails.getPrescription());
        medicalRecord.setUpdateTime(LocalDateTime.now());
        
        return medicalRecordRepository.save(medicalRecord);
    }
}
