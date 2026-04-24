package com.example.dto.dto;


public record StudentResponseDTO(
        Long id,
        String name,
        String email,
        Integer age,
        String courseName,
        String courseCode
) {}