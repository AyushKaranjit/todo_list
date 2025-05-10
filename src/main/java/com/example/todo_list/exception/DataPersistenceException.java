package com.example.todo_list.exception;

// Exception thrown when there is an error with data persistence operations.

public class DataPersistenceException extends RuntimeException {

    
    // Constructs a DataPersistenceException with a message and cause.

    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

   
    // Constructs a DataPersistenceException with a message.

    public DataPersistenceException(String message) {
        super(message);
    }
} 