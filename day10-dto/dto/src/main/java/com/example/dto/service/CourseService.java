package com.example.dto.service;
import com.example.dto.dto.CourseRequestDTO;
import com.example.dto.dto.CourseResponseDTO;
import com.example.dto.exception.BadRequestException;
import com.example.dto.exception.ResourceNotFoundException;
import com.example.dto.model.Course;
import com.example.dto.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CourseResponseDTO getCourseById(Long id) {
        Course course = findCourseEntity(id);
        return toResponseDTO(course);
    }

    public CourseResponseDTO createCourse(CourseRequestDTO request) {
        if (courseRepository.existsByCode(request.code())) {
            throw new BadRequestException(
                    "Course code already exists: " + request.code());
        }

        Course course = new Course(request.name(), request.code());
        Course saved = courseRepository.save(course);

        return toResponseDTO(saved);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO request) {
        Course existing = findCourseEntity(id);
        existing.setName(request.name());
        existing.setCode(request.code());

        Course updated = courseRepository.save(existing);
        return toResponseDTO(updated);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", "id", id);
        }
        courseRepository.deleteById(id);
    }

    // Internal helper
    public Course findCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    // Entity → DTO conversion
    private CourseResponseDTO toResponseDTO(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getStudents().size()
        );
    }
}
