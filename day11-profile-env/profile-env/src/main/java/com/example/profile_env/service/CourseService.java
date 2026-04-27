package com.example.profile_env.service;


import com.example.profile_env.dto.CourseRequestDTO;
import com.example.profile_env.dto.CourseResponseDTO;
import com.example.profile_env.exception.BadRequestException;
import com.example.profile_env.exception.ResourceNotFoundException;
import com.example.profile_env.model.Course;
import com.example.profile_env.repository.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public CourseService(CourseRepository courseRepository, ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CourseResponseDTO getCourseById(Long id) {
        return toResponseDTO(findCourseEntity(id));
    }

    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new BadRequestException(
                    "Course code already exists: " + request.getCode());
        }

        Course course = modelMapper.map(request, Course.class);
        Course saved = courseRepository.save(course);
        return toResponseDTO(saved);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        Course existing = findCourseEntity(id);
        modelMapper.map(request, existing);
        Course updated = courseRepository.save(existing);
        return toResponseDTO(updated);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        courseRepository.deleteById(id);
    }

    public Course findCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    private CourseResponseDTO toResponseDTO(Course course) {
        CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
        dto.setStudentCount(course.getStudents().size());
        return dto;
    }
}