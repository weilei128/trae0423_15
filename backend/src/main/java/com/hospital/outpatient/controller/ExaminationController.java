package com.hospital.outpatient.controller;

import com.hospital.outpatient.entity.Examination;
import com.hospital.outpatient.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/examinations")
@CrossOrigin(origins = "http://localhost:10015")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Examination> createExamination(@RequestBody Examination examination) {
        Examination created = examinationService.createExamination(examination);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Examination> getExaminationById(@PathVariable Long id) {
        Examination examination = examinationService.getExaminationById(id);
        return ResponseEntity.ok(examination);
    }

    @GetMapping("/no/{examNo}")
    public ResponseEntity<Examination> getExaminationByNo(@PathVariable String examNo) {
        Examination examination = examinationService.getExaminationByNo(examNo);
        return ResponseEntity.ok(examination);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Examination>> getMyExaminations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientUsername = authentication.getName();
        
        List<Examination> examinations = examinationService.getPatientExaminations(patientUsername);
        return ResponseEntity.ok(examinations);
    }

    @GetMapping("/medical-record/{medicalRecordId}")
    public ResponseEntity<List<Examination>> getExaminationsByMedicalRecord(@PathVariable Long medicalRecordId) {
        List<Examination> examinations = examinationService.getExaminationsByMedicalRecord(medicalRecordId);
        return ResponseEntity.ok(examinations);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'REGISTRAR')")
    public ResponseEntity<Examination> updateExaminationStatus(
            @PathVariable Long id,
            @RequestParam Examination.ExamStatus status) {
        Examination updated = examinationService.updateExaminationStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/result")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Examination> updateExamResult(
            @PathVariable Long id,
            @RequestBody Map<String, String> result) {
        String examResult = result.get("examResult");
        String reportUrl = result.get("reportUrl");
        
        Examination updated = examinationService.updateExamResult(id, examResult, reportUrl);
        return ResponseEntity.ok(updated);
    }
}
