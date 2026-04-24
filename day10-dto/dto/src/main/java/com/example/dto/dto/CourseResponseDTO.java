package com.example.dto.dto;


public record CourseResponseDTO(
        Long id,
        String name,
        String code,
        int studentCount
) {}