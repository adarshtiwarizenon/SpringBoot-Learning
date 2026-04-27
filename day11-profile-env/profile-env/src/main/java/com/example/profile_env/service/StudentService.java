package com.example.profile_env.service;


import com.example.profile_env.dto.StudentRequestDTO;
import com.example.profile_env.dto.StudentResponseDTO;
import com.example.profile_env.exception.BadRequestException;
import com.example.profile_env.exception.ResourceNotFoundException;
import com.example.profile_env.model.Course;
import com.example.profile_env.model.Student;
import com.example.profile_env.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseService courseService;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository,
                          CourseService courseService,
                          ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.courseService = courseService;
        this.modelMapper = modelMapper;
    }

    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StudentResponseDTO getStudentById(Long id) {
        return toResponseDTO(findStudentEntity(id));
    }

    public List<StudentResponseDTO> getStudentsByCourse(Long courseId) {
        return studentRepository.findByCourseId(courseId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email already exists: " + request.getEmail());
        }

        Course course = courseService.findCourseEntity(request.getCourseId());

        Student student = modelMapper.map(request, Student.class);
        student.setCourse(course);

        Student saved = studentRepository.save(student);
        return toResponseDTO(saved);
    }

    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student existing = findStudentEntity(id);

        modelMapper.map(request, existing);

        if (request.getCourseId() != null) {
            Course course = courseService.findCourseEntity(request.getCourseId());
            existing.setCourse(course);
        }

        Student updated = studentRepository.save(existing);
        return toResponseDTO(updated);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

    private Student findStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private StudentResponseDTO toResponseDTO(Student student) {
        return modelMapper.map(student, StudentResponseDTO.class);
    }
}