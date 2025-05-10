package com.example.todo_list.exception;

public class AppInitializationException extends RuntimeException {

    public AppInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
} 