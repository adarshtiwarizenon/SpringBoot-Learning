package com.example.jpa_hibernate.service;
import com.example.jpa_hibernate.exception.StudentNotFoundException;
import com.example.jpa_hibernate.model.Student;
import com.example.jpa_hibernate.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student createStudent(Student student) {
        studentRepository.findByEmail(student.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException(
                    "Email already exists: " + student.getEmail());
        });
        student.setId(null);
        return studentRepository.save(student);
    }
    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);  // throws 404 if not found

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setCourse(updated.getCourse());

        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }
}