package com.example.todo_list;

import java.time.LocalDate;

public class DetailedTask extends Task {
    private String details;

    public DetailedTask(String description, String details) {
        super(description);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details != null ? details.trim() : null;
    }

    @Override
    public String getType() {
        return "Detailed";
    }

    @Override
    public String toString() {
        return super.toString() + " [Details: " + (details != null && !details.isEmpty() ? details : "N/A") + "]";
    }
} 