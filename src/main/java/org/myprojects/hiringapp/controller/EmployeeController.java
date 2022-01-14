package org.myprojects.hiringapp.controller;

import org.myprojects.hiringapp.exception.AddEmployeeRequestException;
import org.myprojects.hiringapp.exception.EmployeeNotFoundException;
import org.myprojects.hiringapp.exception.InvalidStateEventException;
import org.myprojects.hiringapp.model.Employee;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.myprojects.hiringapp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping("/rest/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> post(@RequestBody Employee employee) throws AddEmployeeRequestException {
        employeeService.addEmployee(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Employee> getAll() {
        return employeeService.getAllEmployees();
    }

    @RequestMapping( value = "/{employeeId}/{event}", produces = MediaType.APPLICATION_JSON_VALUE, method = PUT)
    public ResponseEntity<Employee> update(@PathVariable String employeeId, @PathVariable EmployeeStateEvent event) throws InvalidStateEventException, EmployeeNotFoundException {
        Employee employee = employeeService.updateEmployeeState(employeeId, event);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

}
