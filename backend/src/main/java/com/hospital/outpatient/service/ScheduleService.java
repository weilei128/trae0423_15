package com.hospital.outpatient.service;

import com.hospital.outpatient.entity.Department;
import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.Schedule;
import com.hospital.outpatient.repository.DepartmentRepository;
import com.hospital.outpatient.repository.DoctorRepository;
import com.hospital.outpatient.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        Doctor doctor = doctorRepository.findById(schedule.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        
        Department department = departmentRepository.findById(schedule.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("科室不存在"));
        
        schedule.setDoctor(doctor);
        schedule.setDepartment(department);
        schedule.setRemainingPatients(schedule.getMaxPatients());
        schedule.setStatus(Schedule.ScheduleStatus.AVAILABLE);
        
        return scheduleRepository.save(schedule);
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("排班不存在"));
    }

    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date);
    }

    public List<Schedule> getSchedulesByDepartmentAndDate(Long departmentId, LocalDate date) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("科室不存在"));
        return scheduleRepository.findByDepartmentAndScheduleDate(department, date);
    }

    public List<Schedule> getSchedulesByDoctorAndDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        return scheduleRepository.findByDoctorAndScheduleDate(doctor, date);
    }

    public List<Schedule> getSchedulesByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("医生不存在"));
        return scheduleRepository.findByDoctor(doctor);
    }

    @Transactional
    public Schedule updateSchedule(Long id, Schedule scheduleDetails) {
        Schedule schedule = getScheduleById(id);
        schedule.setScheduleDate(scheduleDetails.getScheduleDate());
        schedule.setStartTime(scheduleDetails.getStartTime());
        schedule.setEndTime(scheduleDetails.getEndTime());
        schedule.setTimeSlot(scheduleDetails.getTimeSlot());
        schedule.setMaxPatients(scheduleDetails.getMaxPatients());
        schedule.setStatus(scheduleDetails.getStatus());
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        schedule.setStatus(Schedule.ScheduleStatus.CANCELLED);
        scheduleRepository.save(schedule);
    }
}
