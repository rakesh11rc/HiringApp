package org.myprojects.hiringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "employee not found")
public class EmployeeNotFoundException extends Exception{

    private String message;

    public EmployeeNotFoundException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
