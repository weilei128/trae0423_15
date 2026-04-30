package com.hospital.outpatient.controller;

import com.hospital.outpatient.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:10015")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/daily/{date}")
    public ResponseEntity<Map<String, Object>> getDailyStatistics(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> stats = dashboardService.getDailyStatistics(date);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayStatistics() {
        Map<String, Object> stats = dashboardService.getDailyStatistics(LocalDate.now());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/department-load/{date}")
    public ResponseEntity<Map<String, Object>> getDepartmentLoad(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> result = dashboardService.getDepartmentLoad(date);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> stats = dashboardService.getRevenueStatistics(startDate, endDate);
        return ResponseEntity.ok(stats);
    }
}
