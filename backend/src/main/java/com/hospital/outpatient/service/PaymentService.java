package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.Examination;
import com.hospital.outpatient.entity.Payment;
import com.hospital.outpatient.entity.User;
import com.hospital.outpatient.repository.AppointmentRepository;
import com.hospital.outpatient.repository.ExaminationRepository;
import com.hospital.outpatient.repository.PaymentRepository;
import com.hospital.outpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Payment createPayment(Payment payment) {
        User patient = userRepository.findById(payment.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        
        payment.setPatient(patient);
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        
        return paymentRepository.save(payment);
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("支付记录不存在"));
    }

    public Payment getPaymentByNo(String paymentNo) {
        return paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new RuntimeException("支付记录不存在"));
    }

    public List<Payment> getPatientPayments(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        return paymentRepository.findByPatientOrderByCreateTimeDesc(patient);
    }

    @Transactional
    public Payment processPayment(Long paymentId, Payment.PaymentMethod paymentMethod, String transactionId) {
        Payment payment = getPaymentById(paymentId);
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("该支付无法处理");
        }

        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        if (payment.getPaymentType() == Payment.PaymentType.EXAMINATION && payment.getExamination() != null) {
            Examination examination = examinationRepository.findById(payment.getExamination().getId())
                    .orElseThrow(() -> new RuntimeException("检查申请不存在"));
            examination.setStatus(Examination.ExamStatus.PAID);
            examinationRepository.save(examination);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("该支付无法取消");
        }

        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        payment.setUpdateTime(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
