package com.example.demo.controllers;

import com.example.demo.entity.Employee;
import com.example.demo.services.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create employee (with or without manager)
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    // Get all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Get one employee
    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id).orElseThrow();
    }

    // Get all subordinates for a given manager
    @GetMapping("/{id}/subordinates")
    public List<Employee> getSubordinates(@PathVariable Long id) {
        return employeeService.getSubordinates(id);
    }

    @PostMapping("/transfer")
    public String transferSalary(@RequestParam Long fromId,
                                 @RequestParam Long toId,
                                 @RequestParam Double amount) {
        try {
            employeeService.transferSalary(fromId, toId, amount);
            return "Transfer successful!";
        } catch (Exception e) {
            return "Transfer failed: " + e.getMessage();
        }
    }


    @PutMapping("/{id}/salary")
    public String updateSalary(@PathVariable Long id, @RequestParam Double salary) {
        try {
            employeeService.updateSalary(id, salary);
            return "Salary updated successfully!";
        } catch (Exception e) {
            return "Failed to update salary: " + e.getMessage();
        }
    }

    // ---------------------------
    @PostMapping("/nested")
    public String nestedTransactionDemo() {
        try {
            employeeService.outerTransaction();
            return "Nested transaction executed!";
        } catch (Exception e) {
            return "Nested transaction failed: " + e.getMessage();
        }
    }

    @PutMapping("/update-employee-department")
    public String updateEmployeeAndDepartment(@RequestParam Long empId,
                                              @RequestParam Double salary,
                                              @RequestParam Long deptId,
                                              @RequestParam String deptName) {
        try {
            employeeService.updateEmployeeAndDepartment(empId, salary, deptId, deptName);
            return "Employee and Department updated successfully!";
        } catch (Exception e) {
            return "Update failed: " + e.getMessage();
        }
    }

}
