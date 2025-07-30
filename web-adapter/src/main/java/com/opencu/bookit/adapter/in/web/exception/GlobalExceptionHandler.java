package com.opencu.bookit.adapter.in.web.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Value("${booking.zone-id}")
    private ZoneId zoneId;

    private ApiError buildError(HttpStatus status, String message, String path, Map<String, Object> details) {
        return new ApiError(
                LocalDateTime.now(zoneId),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("Validation failed: {}", errors);
        ApiError apiError = buildError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", errors)
                                      );
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        logger.info("Resource not found: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
                                      );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        logger.error("Unexpected error", ex);
        ApiError apiError = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
                                      );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(ProfileNotCompletedException.class)
    public ResponseEntity<ApiError> handleProfileNotCompletedException(ProfileNotCompletedException ex, WebRequest request) {
        logger.warn("Profile not completed: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        logger.warn("No such element found: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiError> handleJsonProcessingException(JsonProcessingException ex, WebRequest request) {
        logger.warn("Internal server error: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Unprocessable entity: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiError);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        logger.warn("Bad request: {}", ex.getMessage());
        ApiError apiError = buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                Map.of("errors", ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}