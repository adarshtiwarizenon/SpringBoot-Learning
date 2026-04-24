package com.example.dto.service;
import com.example.dto.dto.StudentRequestDTO;
import com.example.dto.dto.StudentResponseDTO;
import com.example.dto.exception.BadRequestException;
import com.example.dto.exception.ResourceNotFoundException;
import com.example.dto.model.Course;
import com.example.dto.model.Student;
import com.example.dto.repository.StudentRepository;
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

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentEntity(id);
        return toResponseDTO(student);
    }

    public List<StudentResponseDTO> getStudentsByCourse(Long courseId) {
        return studentRepository.findByCourseId(courseId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new BadRequestException(
                    "Email already exists: " + request.email());
        }

        Course course = courseService.findCourseEntity(request.courseId());

        Student student = new Student(
                request.name(),
                request.email(),
                request.age()
        );
        student.setCourse(course);

        Student saved = studentRepository.save(student);
        return toResponseDTO(saved);
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student existing = findStudentEntity(id);

        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setAge(request.age());

        Course course = courseService.findCourseEntity(request.courseId());
        existing.setCourse(course);

        Student updated = studentRepository.save(existing);
        return toResponseDTO(updated);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

    // Internal helper
    private Student findStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    // Entity → DTO conversion
    private StudentResponseDTO toResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCourse().getName(),
                student.getCourse().getCode()
        );
    }
}