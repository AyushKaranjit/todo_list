package com.example.todo_list.exception;

// Custom exception for errors during application initialization.
public class AppInitializationException extends RuntimeException {

    // Constructs an AppInitializationException with a message and cause.
    public AppInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
} 