package com.example.entity_relationship.service;

import com.example.entity_relationship.exception.ResourceNotFoundException;
import com.example.entity_relationship.model.Course;
import com.example.entity_relationship.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + id));
    }

    public Course createCourse(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new IllegalArgumentException(
                    "Course code already exists: " + course.getCode());
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updated) {
        Course existing = getCourseById(id);
        existing.setName(updated.getName());
        existing.setCode(updated.getCode());
        return courseRepository.save(existing);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}