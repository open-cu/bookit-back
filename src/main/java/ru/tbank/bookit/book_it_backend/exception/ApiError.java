package ru.tbank.bookit.book_it_backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiError {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
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
