package com.example.studentapi.controller;
import com.example.studentapi.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final List<Student> students = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // GET all
    @GetMapping
    public ResponseEntity<List<Student>> getAll() {
        return ResponseEntity.ok(students);
    }

    // GET one
    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST
    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        student.setId(idCounter.getAndIncrement());
        students.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Student> update(
            @PathVariable Long id,
            @RequestBody Student updated) {

        for (Student s : students) {
            if (s.getId().equals(id)) {
                s.setName(updated.getName());
                s.setEmail(updated.getEmail());
                s.setCourse(updated.getCourse());
                return ResponseEntity.ok(s);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = students.removeIf(s -> s.getId().equals(id));
        return removed
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}