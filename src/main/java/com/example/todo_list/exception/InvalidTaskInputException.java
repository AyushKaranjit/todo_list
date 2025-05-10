package com.example.todo_list.exception;

// Custom exception for invalid input related to tasks.
public class InvalidTaskInputException extends IllegalArgumentException {

    // Constructs an InvalidTaskInputException with a message.
    public InvalidTaskInputException(String message) {
        super(message);
    }

    // Constructs an InvalidTaskInputException with a message and cause.
    public InvalidTaskInputException(String message, Throwable cause) {
        super(message, cause);
    }
} 