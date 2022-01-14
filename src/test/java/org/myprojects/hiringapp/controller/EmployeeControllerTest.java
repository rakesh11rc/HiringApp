package org.myprojects.hiringapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.myprojects.hiringapp.model.Contract;
import org.myprojects.hiringapp.model.Employee;
import org.myprojects.hiringapp.model.EmployeeState;
import org.myprojects.hiringapp.model.EmployeeStateEvent;
import org.myprojects.hiringapp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    private static Contract contract;

    private static Employee employee;

    private static Employee employee2;

    ObjectMapper objectMapper = new ObjectMapper();

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

        employee2 = Employee.builder()
                .firstName("Rakesh")
                .lastName("Chandru")
                .age(18)
                .contract(contract)
                .build();
    }

    @Test
    void testAddEmployee() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/rest/v1/employee")
                        .content(asJsonString(employee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        Employee addedEmployee = objectMapper.readValue(contentAsString, Employee.class);

        assertThat(addedEmployee.getFirstName()).isEqualTo("Martin");
        assertThat(addedEmployee.getLastName()).isEqualTo("Cook");
        assertThat(addedEmployee.getAge()).isEqualTo(35);
        assertThat(addedEmployee.getEmployeeState()).isEqualTo(EmployeeState.ADDED);
        assertThat(addedEmployee.getContract().getId()).isEqualTo(addedEmployee.getId());
        assertThat(addedEmployee.getContract().getBeginDate())
                .isEqualTo(new GregorianCalendar(2022, Calendar.FEBRUARY, 1).getTime());
        assertThat(addedEmployee.getContract().getCompensation()).isEqualTo(100000);
        assertThat(addedEmployee.getContract().getInfo()).isEqualTo("Marketing Manager contract");
    }

    @Test
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rest/v1/employee")
                        .content(asJsonString(employee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/rest/v1/employee")
                        .content(asJsonString(employee2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/rest/v1/employee"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        List<Employee> employeeList = objectMapper.readValue(contentAsString, List.class);

        assertThat(employeeList.size()).isEqualTo(2);
    }

    @Test
    void testUpdateEmployeeState() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/rest/v1/employee")
                        .content(asJsonString(employee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        Employee addedEmployee = objectMapper.readValue(contentAsString, Employee.class);

        result = mockMvc.perform(MockMvcRequestBuilders
                        .put("/rest/v1/employee/{employeeId}/{event}", addedEmployee.getId(), EmployeeStateEvent.CHECK)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        contentAsString = result.getResponse().getContentAsString();

        addedEmployee = objectMapper.readValue(contentAsString, Employee.class);

        assertThat(addedEmployee.getEmployeeState()).isEqualTo(EmployeeState.INCHECK);


    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
