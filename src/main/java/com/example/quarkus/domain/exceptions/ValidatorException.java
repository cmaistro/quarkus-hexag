package com.example.quarkus.domain.exceptions;

import lombok.Getter;

@Getter
public class ValidatorException extends RuntimeException {

    private String errorCode;
    private String message;
    private String field;

    public ValidatorException(String message, String errorCode, String field) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.field = field;
    }

    @Override
    public String getMessage() {
        return String.format("Error validating field '%s': %s - %s", this.field, this.errorCode, this.message);
    }

}
