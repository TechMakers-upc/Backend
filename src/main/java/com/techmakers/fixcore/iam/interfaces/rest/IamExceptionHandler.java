package com.techmakers.fixcore.iam.interfaces.rest;

import com.techmakers.fixcore.iam.domain.exceptions.InvalidCredentialsException;
import com.techmakers.fixcore.shared.interfaces.rest.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class IamExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        var error = new ApiError(401, ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
