package com.example.demo.services;

import com.example.demo.entity.Employee;
import com.example.demo.repo.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getSubordinates(Long managerId) {
        return employeeRepository.findByManagerId(managerId);
    }

    public Optional<Employee> getEmployee(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    public void transferSalary(Long fromEmployeeId, Long toEmployeeId, Double amount) {
        Employee from = employeeRepository.findById(fromEmployeeId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Employee to = employeeRepository.findById(toEmployeeId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Deduct from sender
        from.setSalary(from.getSalary() - amount);
        employeeRepository.save(from);

        // Simulate exception
        if (amount > 1000) {
            throw new RuntimeException("Transfer amount too high, rolling back!");
        }

        // Add to receiver
        to.setSalary(to.getSalary() + amount);
        employeeRepository.save(to);
    }


    @Transactional
    public void updateSalary(Long employeeId, Double newSalary) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setSalary(newSalary);
    }

    @Transactional
    public void outerTransaction() {
        Employee e1 = new Employee("Alice", 5000.0);
        employeeRepository.save(e1);

        try {
            innerTransaction();  // Calls another transactional method
        } catch (Exception e) {
            System.out.println("Exception in innerTransaction caught");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerTransaction() {
        Employee e2 = new Employee("Bob", 3000.0);
        employeeRepository.save(e2);
        throw new RuntimeException("Force rollback inner transaction");
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferWithCheckedException(Long fromId, Long toId, Double amount) throws Exception {
        Employee from = employeeRepository.findById(fromId).orElseThrow();
        Employee to = employeeRepository.findById(toId).orElseThrow();

        from.setSalary(from.getSalary() - amount);
        to.setSalary(to.getSalary() + amount);

        employeeRepository.save(from);
        employeeRepository.save(to);

        // Checked exception triggers rollback
        if (amount > 1000) throw new Exception("Amount too high");
    }

    @Transactional
    public void updateEmployeeAndDepartment(Long empId, Double salary, Long deptId, String deptName) {
        Employee emp = employeeRepository.findById(empId).orElseThrow();
        emp.setSalary(salary);
        employeeRepository.save(emp);

//        Department dept = departmentRepository.findById(deptId).orElseThrow();
//        dept.setName(deptName);
//        departmentRepository.save(dept);
    }



}
