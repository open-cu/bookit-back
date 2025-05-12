package ru.tbank.bookit.book_it_backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Structured error response")
public class ApiError {
    @Schema(description = "Time when the error occurred", example = "2025-04-24T13:15:00")
    private final LocalDateTime timestamp;

    @Schema(description = "HTTP status", example = "404")
    private final int status;

    @Schema(description = "Type of error", example = "Not Found")
    private final String error;

    @Schema(description = "Error message", example = "Resource not found")
    private final String message;

    @Schema(description = "Request path", example = "/booking-menu/booking")
    private final String path;

    @Schema(description = "Additional details")
    private final Map<String, Object> details;

    public ApiError(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, Object> getDetails() { return details; }
}
