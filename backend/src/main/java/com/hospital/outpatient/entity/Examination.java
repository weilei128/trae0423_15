package com.hospital.outpatient.entity;

import javax.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "examinations")
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String examNo;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @Column(nullable = false)
    private String examType;
    
    @Column(nullable = false)
    private String examName;
    
    @Column(columnDefinition = "TEXT")
    private String examDescription;
    
    private BigDecimal fee;
    
    @Enumerated(EnumType.STRING)
    private ExamStatus status = ExamStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String examResult;
    
    private String reportUrl;
    
    private LocalDateTime examTime;
    
    private LocalDateTime reportTime;
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime;
    
    public enum ExamStatus {
        PENDING,
        PAID,
        IN_PROGRESS,
        COMPLETED,
        REPORTED
    }
}
