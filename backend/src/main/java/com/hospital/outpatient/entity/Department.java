package com.hospital.outpatient.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private String location;
    
    private Integer sortOrder = 0;
    
    private Boolean enabled = true;
    
    private LocalDateTime createTime = LocalDateTime.now();
}
