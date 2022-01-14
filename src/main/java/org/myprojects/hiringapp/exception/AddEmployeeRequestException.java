package org.myprojects.hiringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid add employee request")
public class AddEmployeeRequestException extends Exception {

    private String message;

    public AddEmployeeRequestException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
