package com.hospital.outpatient.dto;

import lombok.Data;
import java.util.List;

@Data
public class DoctorDTO {
    private Long id;
    private UserDTO user;
    private String title;
    private String specialty;
    private String introduction;
    private String avatar;
    private Integer consultationFee;
    private Boolean available;
}
