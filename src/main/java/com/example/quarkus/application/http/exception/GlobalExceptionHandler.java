package com.example.quarkus.application.http.exception;

import com.example.quarkus.application.http.dto.GeneralError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        GeneralError generalError = new GeneralError();
        generalError.setErrorCode("ERR-999");
        generalError.setMessage("Unexpected error processing your request");
        generalError.setDeveloperMessage(exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE)
                .entity(generalError).build();
    }
}
