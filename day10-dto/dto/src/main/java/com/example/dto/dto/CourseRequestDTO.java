package com.example.dto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequestDTO(

        @NotBlank(message = "Course name is required")
        @Size(min = 3, max = 100, message = "Course name must be 3-100 characters")
        String name,

        @NotBlank(message = "Course code is required")
        @Size(min = 3, max = 10, message = "Course code must be 3-10 characters")
        String code

) {}