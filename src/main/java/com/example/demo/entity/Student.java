package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private int standard;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private int age;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "student_courses", // join table
            joinColumns = @JoinColumn(name = "student_id"), // FK in join table
            inverseJoinColumns = @JoinColumn(name = "course_id") // FK in join table
    )

//    @JsonIgnoreProperties("students") // Prevent infinite recursion in JSON
    @JsonIgnore
    private Set<Course> courses = new HashSet<>();

    public Student() {}

    public Student(String name, int standard, String email, int age) {
        this.name = name;
        this.standard = standard;
        this.email = email;
        this.age = age;
    }

    // Add helper methods
    public void enrollCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }

    // Getters and Setters
    // (You can generate using Lombok @Getter @Setter if preferred)

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStandard() {
        return standard;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
