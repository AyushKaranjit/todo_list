package com.example.todo_list;

import java.time.LocalDate;

// Represents a task that includes additional details along with its description.
public class DetailedTask extends Task {
    private String details;

    // Constructs a DetailedTask with a description and details.
    public DetailedTask(String description, String details) {
        super(description);
        this.details = details;
    }

    // Gets the details of the task.
    public String getDetails() {
        return details;
    }

    // Sets the details of the task.
    public void setDetails(String details) {
        this.details = details != null ? details.trim() : null;
    }

    // Returns the type of the task.
    @Override
    public String getType() {
        return "Detailed";
    }

    // Returns a string representation of the DetailedTask.
    @Override
    public String toString() {
        return super.toString() + " [Details: " + (details != null && !details.isEmpty() ? details : "N/A") + "]";
    }
} 