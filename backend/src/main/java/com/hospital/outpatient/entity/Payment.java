package com.hospital.outpatient.entity;

import javax.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String paymentNo;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @ManyToOne
    @JoinColumn(name = "examination_id")
    private Examination examination;
    
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private String transactionId;
    
    private LocalDateTime paymentTime;
    
    private LocalDateTime createTime = LocalDateTime.now();
    
    private LocalDateTime updateTime;
    
    public enum PaymentType {
        CONSULTATION,
        EXAMINATION,
        PRESCRIPTION
    }
    
    public enum PaymentMethod {
        WECHAT,
        ALIPAY,
        CASH,
        CARD
    }
    
    public enum PaymentStatus {
        PENDING,
        PAID,
        REFUNDED,
        CANCELLED
    }
}
