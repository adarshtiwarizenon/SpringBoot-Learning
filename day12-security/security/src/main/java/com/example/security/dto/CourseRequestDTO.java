package com.example.security.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseRequestDTO {

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100, message = "Course name must be 3-100 characters")
    private String name;

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 10, message = "Course code must be 3-10 characters")
    private String code;

    public CourseRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}