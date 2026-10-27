package com.techmakers.fixcore.shared.interfaces.rest;

import com.techmakers.fixcore.shared.domain.exceptions.BusinessRuleViolationException;
import com.techmakers.fixcore.shared.domain.exceptions.PlantAccessDeniedException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceAlreadyExistsException;
import com.techmakers.fixcore.shared.domain.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        var error = new ApiError(
                400,
                "Validation failed",
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        var error = new ApiError(
                400,
                "Validation failed",
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidBody(HttpMessageNotReadableException ex) {
        var error = new ApiError(
                400,
                "Invalid request body",
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        var error = new ApiError(404, ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class, BusinessRuleViolationException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex) {
        var error = new ApiError(409, ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PlantAccessDeniedException.class)
    public ResponseEntity<ApiError> handlePlantAccessDenied(PlantAccessDeniedException ex) {
        var error = new ApiError(403, ex.getMessage(), Instant.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        var error = new ApiError(400, ex.getMessage(), Instant.now());
        return ResponseEntity.badRequest().body(error);
    }
}