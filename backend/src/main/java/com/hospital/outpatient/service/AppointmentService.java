package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.Schedule;
import com.hospital.outpatient.entity.User;
import com.hospital.outpatient.repository.AppointmentRepository;
import com.hospital.outpatient.repository.DoctorRepository;
import com.hospital.outpatient.repository.ScheduleRepository;
import com.hospital.outpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Appointment createAppointment(Long scheduleId, String patientUsername) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("排班不存在"));
        
        if (schedule.getStatus() == Schedule.ScheduleStatus.CANCELLED) {
            throw new RuntimeException("该排班已取消");
        }
        
        if (schedule.getRemainingPatients() <= 0) {
            throw new RuntimeException("号源已满");
        }

        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        
        Doctor doctor = schedule.getDoctor();

        Appointment appointment = new Appointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSchedule(schedule);
        appointment.setDepartment(schedule.getDepartment());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setAppointmentTime(schedule.getStartTime());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        
        List<Appointment> existingAppointments = appointmentRepository.findByScheduleId(scheduleId);
        appointment.setQueueNumber(existingAppointments.size() + 1);

        schedule.setRemainingPatients(schedule.getRemainingPatients() - 1);
        if (schedule.getRemainingPatients() == 0) {
            schedule.setStatus(Schedule.ScheduleStatus.FULL);
        }
        scheduleRepository.save(schedule);

        return appointmentRepository.save(appointment);
    }

    private String generateAppointmentNo() {
        return "APT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
    }

    public Appointment getAppointmentByNo(String appointmentNo) {
        return appointmentRepository.findByAppointmentNo(appointmentNo)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
    }

    public List<Appointment> getPatientAppointments(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new RuntimeException("患者不存在"));
        return appointmentRepository.findByPatientOrderByCreateTimeDesc(patient);
    }

    public List<Appointment> getDoctorAppointmentsByDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        return appointmentRepository.findByDoctorAndAppointmentDateOrderByQueueNumber(doctor, date);
    }

    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED ||
            appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("该预约无法取消");
        }

        Schedule schedule = appointment.getSchedule();
        schedule.setRemainingPatients(schedule.getRemainingPatients() + 1);
        if (schedule.getStatus() == Schedule.ScheduleStatus.FULL) {
            schedule.setStatus(Schedule.ScheduleStatus.AVAILABLE);
        }
        scheduleRepository.save(schedule);

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setUpdateTime(LocalDateTime.now());
        
        return appointmentRepository.save(appointment);
    }
}
