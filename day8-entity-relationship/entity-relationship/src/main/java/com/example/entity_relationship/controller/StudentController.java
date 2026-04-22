package com.example.entity_relationship.controller;

import com.example.entity_relationship.model.Student;
import com.example.entity_relationship.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Student>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getStudentsByCourse(courseId));
    }

    @PostMapping
    public ResponseEntity<Student> create(
            @RequestBody Student student,
            @RequestParam Long courseId) {

        Student created = studentService.createStudent(student, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(
            @PathVariable Long id,
            @RequestBody Student student,
            @RequestParam(required = false) Long courseId) {

        return ResponseEntity.ok(studentService.updateStudent(id, student, courseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}