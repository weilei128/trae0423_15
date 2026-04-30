package com.hospital.outpatient.repository;

import com.hospital.outpatient.entity.Appointment;
import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByCreateTimeDesc(User patient);
    List<Appointment> findByDoctorAndAppointmentDateOrderByQueueNumber(Doctor doctor, LocalDate appointmentDate);
    List<Appointment> findByScheduleId(Long scheduleId);
    Optional<Appointment> findByAppointmentNo(String appointmentNo);
    List<Appointment> findByAppointmentDate(LocalDate date);
    long countByAppointmentDateAndStatus(LocalDate date, Appointment.AppointmentStatus status);
}
