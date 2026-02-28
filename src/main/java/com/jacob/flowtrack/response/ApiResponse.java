package com.jacob.flowtrack.response;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private String timestamp;
    private T data;

    public ApiResponse(boolean success, String message, String timestamp, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, LocalDateTime.now().toString(), data);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }
}
