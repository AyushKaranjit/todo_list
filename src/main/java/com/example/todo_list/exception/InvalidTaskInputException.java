package com.example.todo_list.exception;

public class InvalidTaskInputException extends IllegalArgumentException {

    public InvalidTaskInputException(String message) {
        super(message);
    }

    public InvalidTaskInputException(String message, Throwable cause) {
        super(message, cause);
    }
} 