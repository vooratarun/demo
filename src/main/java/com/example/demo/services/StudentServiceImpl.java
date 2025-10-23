package com.example.demo.services;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.repo.StudentRepository;
import com.example.demo.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;


    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }


    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }


    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }


    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }


    public Student updateStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(student.getName());
                    existingStudent.setStandard(student.getStandard());
                    existingStudent.setEmail(student.getEmail());
                    existingStudent.setAge(student.getAge());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }


    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public Student enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.enrollCourse(course);
        return studentRepository.save(student);
    }

    public List<Course> getCoursesForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return List.copyOf(student.getCourses());
    }
}
