package com.jacob.flowtrack.exception;

public class ApiError {

    private String message;
    private int status;
    private String timestamp;

    public ApiError(int status, String message, String timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
