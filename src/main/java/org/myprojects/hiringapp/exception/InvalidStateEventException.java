package org.myprojects.hiringapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "invalid employee state event")
public class InvalidStateEventException extends Exception{

    private String message;

    public InvalidStateEventException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
