package com.example.todo_list;

// Represents a basic task with only a description.
public class SimpleTask extends Task {

    // Constructs a SimpleTask with a description.
    public SimpleTask(String description) {
        super(description);
    }

    // Returns the type of the task.
    @Override
    public String getType() {
        return "Simple";
    }
} 