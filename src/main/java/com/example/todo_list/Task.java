package com.example.todo_list;

import com.example.todo_list.exception.InvalidTaskInputException;
import java.time.LocalDate;

// Abstract base class for all types of tasks.
public abstract class Task {
    private String description;
    private LocalDate reminderDate;
    private boolean completed;

    // Constructs a Task with a description.
    public Task(String description) {
        this.description = description;
        this.completed = false;
        this.reminderDate = null;
    }

    // Gets the description of the task.
    public String getDescription() {
        return description;
    }

    // Gets the reminder date of the task.
    public LocalDate getReminderDate() {
        return reminderDate;
    }

    // Sets the reminder date of the task.
    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    // Checks if the task is completed.
    public boolean isCompleted() {
        return completed;
    }

    // Sets the completion status of the task.
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Sets the description of the task.
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidTaskInputException("Description cannot be empty.");
        }
        this.description = description.trim();
    }

    // Abstract method to get the type of the task (e.g., "Simple", "Detailed").
    public abstract String getType();

    // Returns a string representation of the Task.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (completed) {
            sb.append("[COMPLETED] ");
        }
        
        sb.append("(").append(getType()).append(") ").append(description);
        
        if (reminderDate != null) {
            sb.append(" (Reminder: ").append(reminderDate.toString()).append(")");
        }
        
        return sb.toString();
    }
}
