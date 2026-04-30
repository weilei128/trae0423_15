package com.hospital.outpatient.dto;

import lombok.Data;

@Data
public class DepartmentDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Integer sortOrder;
    private Boolean enabled;
}
