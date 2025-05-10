package com.example.todo_list;

import java.time.LocalDate;

public abstract class Task {
    private String description;
    private LocalDate reminderDate;
    private boolean completed;

    public Task(String description) {
        this.description = description;
        this.completed = false;
        this.reminderDate = null;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public abstract String getType();

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
