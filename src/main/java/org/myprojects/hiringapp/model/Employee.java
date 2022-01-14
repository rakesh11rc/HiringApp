package org.myprojects.hiringapp.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Employee {

    private String id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private int age;

    @NonNull
    private Contract contract;

    private EmployeeState employeeState;

}
