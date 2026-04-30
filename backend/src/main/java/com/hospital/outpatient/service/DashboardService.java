package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.Department;
import com.hospital.outpatient.entity.Payment;
import com.hospital.outpatient.repository.AppointmentRepository;
import com.hospital.outpatient.repository.DepartmentRepository;
import com.hospital.outpatient.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Object> getDailyStatistics(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalAppointments = appointmentRepository.countByAppointmentDateAndStatus(date, Appointment.AppointmentStatus.COMPLETED);
        long checkedInCount = appointmentRepository.countByAppointmentDateAndStatus(date, Appointment.AppointmentStatus.CHECKED_IN);
        long completedCount = appointmentRepository.countByAppointmentDateAndStatus(date, Appointment.AppointmentStatus.COMPLETED);
        long noShowCount = appointmentRepository.countByAppointmentDateAndStatus(date, Appointment.AppointmentStatus.NO_SHOW);
        
        stats.put("totalAppointments", totalAppointments + checkedInCount + completedCount + noShowCount);
        stats.put("completedCount", completedCount);
        stats.put("checkedInCount", checkedInCount);
        stats.put("noShowCount", noShowCount);
        
        double noShowRate = (totalAppointments + checkedInCount + completedCount + noShowCount) > 0 
                ? (double) noShowCount / (totalAppointments + checkedInCount + completedCount + noShowCount) * 100 
                : 0.0;
        stats.put("noShowRate", String.format("%.2f", noShowRate));
        
        return stats;
    }

    public Map<String, Object> getDepartmentLoad(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        List<Department> departments = departmentRepository.findByEnabledTrueOrderBySortOrder();
        
        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);
        
        Map<String, Integer> departmentLoad = new HashMap<>();
        for (Department dept : departments) {
            long count = appointments.stream()
                    .filter(a -> a.getDepartment().getId().equals(dept.getId()))
                    .count();
            departmentLoad.put(dept.getName(), (int) count);
        }
        
        result.put("departments", departments.stream().map(Department::getName).collect(Collectors.toList()));
        result.put("loadData", departmentLoad);
        
        return result;
    }

    public Map<String, Object> getRevenueStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.PAID)
                .filter(p -> {
                    if (p.getPaymentTime() == null) return false;
                    LocalDate paymentDate = p.getPaymentTime().toLocalDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
        
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalRevenue", totalRevenue);
        stats.put("paymentCount", payments.size());
        
        return stats;
    }
}
