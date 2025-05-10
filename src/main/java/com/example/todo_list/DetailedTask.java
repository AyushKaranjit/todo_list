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
        this.details = details;
    }

    @Override
    public String getType() {
        return "Detailed";
    }

    // Optionally, override toString() to include details if needed for display
    // Or rely on a more sophisticated cell factory in the controller
    @Override
    public String toString() {
        return super.toString() + " [Details: " + (details != null && !details.isEmpty() ? details : "N/A") + "]";
    }
} 