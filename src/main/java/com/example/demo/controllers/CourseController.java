package com.example.demo.controllers;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // ✅ Create a new course
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // ✅ Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // ✅ Get students enrolled in a specific course
    @GetMapping("/{courseId}/students")
    public Set<Student> getStudentsInCourse(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        System.out.println("course detials"+course);

        return course.getStudents();
    }
}
