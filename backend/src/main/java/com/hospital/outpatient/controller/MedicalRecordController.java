package com.hospital.outpatient.controller;

import com.hospital.outpatient.entity.MedicalRecord;
import com.hospital.outpatient.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:10015")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord created = medicalRecordService.createMedicalRecord(medicalRecord);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(id);
        return ResponseEntity.ok(medicalRecord);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecord> getMedicalRecordByAppointment(@PathVariable Long appointmentId) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByAppointment(appointmentId);
        return ResponseEntity.ok(medicalRecord);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MedicalRecord>> getMyMedicalRecords() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientUsername = authentication.getName();
        
        List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecords(patientUsername);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord medicalRecordDetails) {
        MedicalRecord updated = medicalRecordService.updateMedicalRecord(id, medicalRecordDetails);
        return ResponseEntity.ok(updated);
    }
}
