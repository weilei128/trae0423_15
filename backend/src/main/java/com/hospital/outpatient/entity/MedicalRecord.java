package com.hospital.outpatient.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(columnDefinition = "TEXT")
    private String chiefComplaint;
    
    @Column(columnDefinition = "TEXT")
    private String presentIllness;
    
    @Column(columnDefinition = "TEXT")
    private String pastHistory;
    
    @Column(columnDefinition = "TEXT")
    private String physicalExamination;
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String treatmentPlan;
    
    @Column(columnDefinition = "TEXT")
    private String prescription;
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime;
}
