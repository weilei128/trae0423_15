package com.hospital.outpatient.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
    
    private String title;
    
    private String specialty;
    
    private String introduction;
    
    private String avatar;
    
    private Integer consultationFee;
    
    private Boolean available = true;
    
    private LocalDateTime createTime = LocalDateTime.now();
}
