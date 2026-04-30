package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.Payment;
import com.hospital.outpatient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPatientOrderByCreateTimeDesc(User patient);
    Optional<Payment> findByPaymentNo(String paymentNo);
    List<Payment> findByAppointmentId(Long appointmentId);
}
