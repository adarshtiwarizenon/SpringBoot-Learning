package com.example.csr.repository;

import com.example.csr.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Student> findAll() {
        return students;
    }

    public Optional<Student> findById(Long id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(idCounter.getAndIncrement());
            students.add(student);
        } else {
            deleteById(student.getId());
            students.add(student);
        }
        return student;
    }

    public boolean deleteById(Long id) {
        return students.removeIf(s -> s.getId().equals(id));
    }

    public boolean existsById(Long id) {
        return students.stream().anyMatch(s -> s.getId().equals(id));
    }

    public Optional<Student> findByEmail(String email) {
        return students.stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst();
    }
}