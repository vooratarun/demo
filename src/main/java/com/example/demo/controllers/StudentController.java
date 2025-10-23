package com.example.demo.controllers;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.services.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentServiceImpl studentService;

    @Autowired
    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully!";
    }

    // ✅ Enroll a student into a course
    @PostMapping("/{studentId}/enroll/{courseId}")
    public Student enrollStudentInCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.enrollStudentInCourse(studentId, courseId);
    }

    // ✅ Get all courses for a student
    @GetMapping("/{studentId}/courses")
    public List<Course> getCoursesForStudent(@PathVariable Long studentId) {
        return studentService.getCoursesForStudent(studentId);
    }
}
