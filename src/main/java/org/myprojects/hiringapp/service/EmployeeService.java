package org.myprojects.hiringapp.service;

import org.myprojects.hiringapp.exception.AddEmployeeRequestException;
import org.myprojects.hiringapp.exception.EmployeeNotFoundException;
import org.myprojects.hiringapp.exception.InvalidStateEventException;
import org.myprojects.hiringapp.model.Employee;
import org.myprojects.hiringapp.model.EmployeeState;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private StateMachineFactory<EmployeeState, EmployeeStateEvent> stateMachineFactory;


    private final Map<String, Employee> employeeMap = new HashMap<>();
    private final Map<String, StateMachine<EmployeeState, EmployeeStateEvent>> machines = new HashMap<>();

    public void addEmployee(final Employee employee) throws AddEmployeeRequestException {
        if(Objects.nonNull(employee.getId())) {
            throw  new AddEmployeeRequestException("Invalid add employee request: contains employee id");
        }

        if(Objects.nonNull(employee.getEmployeeState())) {
            throw  new AddEmployeeRequestException("Invalid add employee request: contains employee state");
        }

        if(Objects.nonNull(employee.getContract().getId())) {
            throw  new AddEmployeeRequestException("Invalid add employee request: contains contract id");
        }

        String uuid = UUID.randomUUID().toString();
        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = getMachine(uuid);

        employee.setId(uuid);
        employee.getContract().setId(uuid);
        employee.setEmployeeState(stateMachine.getState().getId());
        employeeMap.put(uuid, employee);
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public Employee updateEmployeeState(final String employeeId, final EmployeeStateEvent event) throws InvalidStateEventException, EmployeeNotFoundException {

        if(!employeeMap.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with provided id is not found");
        }

        StateMachine<EmployeeState, EmployeeStateEvent> stateMachine = getMachine(employeeId);
        if(!stateMachine.sendEvent(event)) {
            throw new InvalidStateEventException("Invalid event for the current employee state");
        }

        Employee employee = employeeMap.get(employeeId);
        employee.setEmployeeState(stateMachine.getState().getId());
        return employee;
    }

    public void deleteEmployee(final String emloyeeId) {
        employeeMap.remove(emloyeeId);
    }

    private synchronized StateMachine<EmployeeState, EmployeeStateEvent> getMachine(String id) {
        StateMachine<EmployeeState, EmployeeStateEvent> machine = machines.get(id);
        if (machine == null) {
            machine = stateMachineFactory.getStateMachine(id);
            machines.put(id, machine);
        }
        return machine;
    }

}
