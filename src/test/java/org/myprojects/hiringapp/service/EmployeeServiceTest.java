package org.myprojects.hiringapp.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.myprojects.hiringapp.exception.AddEmployeeRequestException;
import org.myprojects.hiringapp.exception.EmployeeNotFoundException;
import org.myprojects.hiringapp.exception.InvalidStateEventException;
import org.myprojects.hiringapp.model.Contract;
import org.myprojects.hiringapp.model.Employee;
import org.myprojects.hiringapp.model.EmployeeState;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    private static Contract contract;

    private static Employee employee;

    @BeforeAll
    public static void setUp() {

        contract = Contract.builder()
        .beginDate(new GregorianCalendar(2022, Calendar.FEBRUARY, 1).getTime())
        .compensation(100000)
        .info("Marketing Manager contract")
        .build();

         employee = Employee.builder()
        .firstName("Martin")
        .lastName("Cook")
        .age(35)
        .contract(contract)
        .build();
    }

    @AfterEach
    public void clear() {
        List<Employee> employeeList = employeeService.getAllEmployees();
        employeeList.stream().forEach(emp -> {
            employeeService.deleteEmployee(emp.getId());
        });
        employee.setId(null);
        employee.setEmployeeState(null);
        employee.getContract().setId(null);
    }

    @Test
    public void testAddEmployeeWithIdThrowsException() {
        Exception exception = assertThrows(AddEmployeeRequestException.class, () -> {
            Employee employeeWithId = Employee.builder()
                    .id("id")
                    .firstName("Martin")
                    .lastName("Cook")
                    .age(35)
                    .contract(contract)
                    .build();

            employeeService.addEmployee(employeeWithId);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid add employee request: contains employee id");

    }

    @Test
    public void testAddEmployeeWithStateThrowsException() {
        Exception exception = assertThrows(AddEmployeeRequestException.class, () -> {
            Employee employeeWithId = Employee.builder()
                    .firstName("Martin")
                    .lastName("Cook")
                    .age(35)
                    .employeeState(EmployeeState.ADDED)
                    .contract(contract)
                    .build();

            employeeService.addEmployee(employeeWithId);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid add employee request: contains employee state");

    }

    @Test
    public void testAddEmployeeWithContractIdThrowsException() {
        Exception exception = assertThrows(AddEmployeeRequestException.class, () -> {
            Contract contractWithId = Contract.builder()
                    .id("id")
                    .beginDate(new GregorianCalendar(2022, Calendar.FEBRUARY, 1).getTime())
                    .compensation(100000)
                    .info("Marketing Manager contract")
                    .build();

            Employee employeeWithContractId = Employee.builder()
                    .firstName("Martin")
                    .lastName("Cook")
                    .age(35)
                    .contract(contractWithId)
                    .build();

            employeeService.addEmployee(employeeWithContractId);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid add employee request: contains contract id");
    }

    @Test
    public void testAddEmployee() throws AddEmployeeRequestException {
       employeeService.addEmployee(employee);

        assertThat(employee.getFirstName()).isEqualTo("Martin");
        assertThat(employee.getLastName()).isEqualTo("Cook");
        assertThat(employee.getAge()).isEqualTo(35);
        assertThat(employee.getEmployeeState()).isEqualTo(EmployeeState.ADDED);
        assertThat(employee.getContract().getId()).isEqualTo(employee.getId());
        assertThat(employee.getContract().getBeginDate())
                .isEqualTo(new GregorianCalendar(2022, Calendar.FEBRUARY, 1).getTime());
        assertThat(employee.getContract().getCompensation()).isEqualTo(100000);
        assertThat(employee.getContract().getInfo()).isEqualTo("Marketing Manager contract");
    }


    @Test
    void getAllEmployees() throws AddEmployeeRequestException {
        employeeService.addEmployee(employee);

        List<Employee> employeeList = employeeService.getAllEmployees();
        assertThat(employeeList.size()).isEqualTo(1);
        assertThat(employeeList.get(0).getFirstName()).isEqualTo("Martin");
        assertThat(employeeList.get(0).getLastName()).isEqualTo("Cook");
        assertThat(employeeList.get(0).getAge()).isEqualTo(35);
        assertThat(employeeList.get(0).getEmployeeState()).isEqualTo(EmployeeState.ADDED);
    }

    @Test
    public void updateEmployeeState() throws AddEmployeeRequestException, InvalidStateEventException, EmployeeNotFoundException {
        employeeService.addEmployee(employee);
        Employee existingEmployee = employeeService.getAllEmployees().get(0);
        existingEmployee = employeeService.updateEmployeeState(existingEmployee.getId(), EmployeeStateEvent.CHECK);
        assertThat(existingEmployee.getEmployeeState()).isEqualTo(EmployeeState.INCHECK);

    }

    @Test
    public void updateEmployeeStateFalseEvent() throws AddEmployeeRequestException{
        employeeService.addEmployee(employee);

        InvalidStateEventException exception = assertThrows(InvalidStateEventException.class, () -> {
            Employee existingEmployee = employeeService.getAllEmployees().get(0);
            employeeService.updateEmployeeState(existingEmployee.getId(), EmployeeStateEvent.APPROVE);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid event for the current employee state");
    }

}
