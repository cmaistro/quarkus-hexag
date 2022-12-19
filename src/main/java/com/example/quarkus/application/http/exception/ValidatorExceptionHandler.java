package com.example.quarkus.application.http.exception;

import com.example.quarkus.application.http.dto.GeneralError;
import com.example.quarkus.domain.exceptions.ValidatorException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidatorExceptionHandler implements ExceptionMapper<ValidatorException> {

    @Override
    public Response toResponse(ValidatorException exception) {
        GeneralError generalError = new GeneralError();
        generalError.setErrorCode(exception.getErrorCode());
        generalError.setMessage(exception.getMessage());
        generalError.setDeveloperMessage("Request values are invalid.");
        return Response.status(422).type(MediaType.APPLICATION_JSON_TYPE).entity(generalError).build();
    }
}
