package com.hospital.outpatient.controller;

import com.hospital.outpatient.entity.Payment;
import com.hospital.outpatient.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:10015")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment created = paymentService.createPayment(payment);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/no/{paymentNo}")
    public ResponseEntity<Payment> getPaymentByNo(@PathVariable String paymentNo) {
        Payment payment = paymentService.getPaymentByNo(paymentNo);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Payment>> getMyPayments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientUsername = authentication.getName();
        
        List<Payment> payments = paymentService.getPatientPayments(patientUsername);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Payment> processPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> paymentInfo) {
        Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(paymentInfo.get("paymentMethod"));
        String transactionId = paymentInfo.get("transactionId");
        
        Payment payment = paymentService.processPayment(id, paymentMethod, transactionId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Payment> cancelPayment(@PathVariable Long id) {
        Payment payment = paymentService.cancelPayment(id);
        return ResponseEntity.ok(payment);
    }
}
