package com.example.todo_list;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DeadlineTask extends Task {
    private LocalTime dueTime;

    public DeadlineTask(String description, LocalDate reminderDate, LocalTime dueTime) {
        super(description);
        if (reminderDate == null && dueTime != null) {
            throw new IllegalArgumentException("Cannot set due time without a reminder date.");
        }
        setReminderDate(reminderDate); // Use setter to associate date
        this.dueTime = dueTime;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalTime dueTime) {
        if (getReminderDate() == null && dueTime != null) {
             throw new IllegalArgumentException("Cannot set due time without a reminder date. Please set reminder date first.");
        }
        this.dueTime = dueTime;
    }

    @Override
    public String getType() {
        return "Deadline";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (dueTime != null) {
            sb.append(" (Due: ").append(dueTime.format(DateTimeFormatter.ofPattern("HH:mm"))).append(")");
        }
        return sb.toString();
    }
} 