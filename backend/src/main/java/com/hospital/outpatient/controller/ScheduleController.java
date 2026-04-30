package com.hospital.outpatient.controller;

import com.hospital.outpatient.dto.DepartmentDTO;
import com.hospital.outpatient.dto.DoctorDTO;
import com.hospital.outpatient.dto.ScheduleDTO;
import com.hospital.outpatient.entity.Department;
import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.Schedule;
import com.hospital.outpatient.repository.DepartmentRepository;
import com.hospital.outpatient.repository.DoctorRepository;
import com.hospital.outpatient.service.ScheduleService;
import com.hospital.outpatient.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "http://localhost:10015")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody Schedule schedule) {
        Schedule created = scheduleService.createSchedule(schedule);
        return ResponseEntity.ok(DTOConverter.toScheduleDTO(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(DTOConverter.toScheduleDTO(schedule));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Schedule> schedules = scheduleService.getSchedulesByDate(date);
        List<ScheduleDTO> result = schedules.stream()
                .map(DTOConverter::toScheduleDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/department/{departmentId}/date/{date}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDepartmentAndDate(
            @PathVariable Long departmentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Schedule> schedules = scheduleService.getSchedulesByDepartmentAndDate(departmentId, date);
        List<ScheduleDTO> result = schedules.stream()
                .map(DTOConverter::toScheduleDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDoctor(@PathVariable Long doctorId) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctor(doctorId);
        List<ScheduleDTO> result = schedules.stream()
                .map(DTOConverter::toScheduleDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Schedule> schedules = scheduleService.getSchedulesByDoctorAndDate(doctorId, date);
        List<ScheduleDTO> result = schedules.stream()
                .map(DTOConverter::toScheduleDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        Schedule updated = scheduleService.updateSchedule(id, scheduleDetails);
        return ResponseEntity.ok(DTOConverter.toScheduleDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<Department> departments = departmentRepository.findByEnabledTrueOrderBySortOrder();
        List<DepartmentDTO> result = departments.stream()
                .map(DTOConverter::toDepartmentDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctors/department/{departmentId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("科室不存在"));
        List<Doctor> doctors = doctorRepository.findByDepartment(department);
        List<DoctorDTO> result = doctors.stream()
                .map(DTOConverter::toDoctorDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
