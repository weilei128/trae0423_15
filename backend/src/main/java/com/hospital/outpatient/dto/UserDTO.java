package com.hospital.outpatient.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realName;
    private String idCard;
    private String phone;
    private String email;
    private Boolean enabled;
}
