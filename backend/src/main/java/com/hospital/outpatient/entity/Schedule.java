package com.hospital.outpatient.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnoreProperties({"department", "hibernateLazyInitializer", "handler"})
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Department department;
    
    @Column(nullable = false)
    private LocalDate scheduleDate;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    private String timeSlot;
    
    @Column(nullable = false)
    private Integer maxPatients;
    
    @Column(nullable = false)
    private Integer remainingPatients;
    
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.AVAILABLE;
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime;
    
    public enum ScheduleStatus {
        AVAILABLE,
        FULL,
        CANCELLED
    }
}
