package com.example.exceptionhandling.service;
import com.example.exceptionhandling.exception.BadRequestException;
import com.example.exceptionhandling.exception.ResourceNotFoundException;
import com.example.exceptionhandling.model.Course;
import com.example.exceptionhandling.model.Student;
import com.example.exceptionhandling.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseService courseService;

    public StudentService(StudentRepository studentRepository,
                          CourseService courseService) {
        this.studentRepository = studentRepository;
        this.courseService = courseService;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    public List<Student> getStudentsByCourse(Long courseId) {
        return studentRepository.findByCourseId(courseId);
    }

    public Student createStudent(Student student, Long courseId) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new BadRequestException(
                    "Email already exists: " + student.getEmail());
        }

        Course course = courseService.getCourseById(courseId);
        student.setCourse(course);

        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updated, Long courseId) {
        Student existing = getStudentById(id);

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setAge(updated.getAge());

        if (courseId != null) {
            Course course = courseService.getCourseById(courseId);
            existing.setCourse(course);
        }

        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }
}