package com.hospital.outpatient.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Long id;
    private DoctorDTO doctor;
    private DepartmentDTO department;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timeSlot;
    private Integer maxPatients;
    private Integer remainingPatients;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
