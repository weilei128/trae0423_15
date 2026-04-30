package com.hospital.outpatient.controller;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "http://localhost:10015")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping
    public ResponseEntity<Appointment> checkIn(@RequestParam String appointmentNo) {
        Appointment appointment = checkInService.checkIn(appointmentNo);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{appointmentId}/start")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Appointment> startConsultation(@PathVariable Long appointmentId) {
        Appointment appointment = checkInService.startConsultation(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Appointment> completeConsultation(@PathVariable Long appointmentId) {
        Appointment appointment = checkInService.completeConsultation(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/{appointmentId}/no-show")
    @PreAuthorize("hasAnyRole('DOCTOR', 'REGISTRAR')")
    public ResponseEntity<Appointment> markNoShow(@PathVariable Long appointmentId) {
        Appointment appointment = checkInService.markNoShow(appointmentId);
        return ResponseEntity.ok(appointment);
    }
}
