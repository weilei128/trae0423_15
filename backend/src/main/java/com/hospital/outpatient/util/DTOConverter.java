package com.hospital.outpatient.util;

import com.hospital.outpatient.dto.*;
import com.hospital.outpatient.entity.*;

public class DTOConverter {

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setIdCard(user.getIdCard());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.getEnabled());
        return dto;
    }

    public static DoctorDTO toDoctorDTO(Doctor doctor) {
        if (doctor == null) return null;
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUser(toUserDTO(doctor.getUser()));
        dto.setTitle(doctor.getTitle());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setIntroduction(doctor.getIntroduction());
        dto.setAvatar(doctor.getAvatar());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setAvailable(doctor.getAvailable());
        return dto;
    }

    public static DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) return null;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setLocation(department.getLocation());
        dto.setSortOrder(department.getSortOrder());
        dto.setEnabled(department.getEnabled());
        return dto;
    }

    public static ScheduleDTO toScheduleDTO(Schedule schedule) {
        if (schedule == null) return null;
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDoctor(toDoctorDTO(schedule.getDoctor()));
        dto.setDepartment(toDepartmentDTO(schedule.getDepartment()));
        dto.setScheduleDate(schedule.getScheduleDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setTimeSlot(schedule.getTimeSlot());
        dto.setMaxPatients(schedule.getMaxPatients());
        dto.setRemainingPatients(schedule.getRemainingPatients());
        dto.setStatus(schedule.getStatus() != null ? schedule.getStatus().name() : null);
        dto.setCreateTime(schedule.getCreateTime());
        dto.setUpdateTime(schedule.getUpdateTime());
        return dto;
    }
}
