package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class CheckInService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment checkIn(String appointmentNo) {
        Appointment appointment = appointmentRepository.findByAppointmentNo(appointmentNo)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("该预约无法签到");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        appointment.setCheckInTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startConsultation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.CHECKED_IN) {
            throw new RuntimeException("该预约无法开始就诊");
        }

        appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
        appointment.setUpdateTime(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeConsultation(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("该预约无法完成就诊");
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setUpdateTime(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment markNoShow(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("该预约无法标记为爽约");
        }

        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        appointment.setUpdateTime(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }
}
