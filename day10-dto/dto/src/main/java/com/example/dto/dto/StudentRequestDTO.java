package com.example.dto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequestDTO(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be 2-50 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @NotNull(message = "Age is required")
        @Min(value = 5, message = "Age must be at least 5")
        Integer age,

        @NotNull(message = "Course ID is required")
        Long courseId

) {}