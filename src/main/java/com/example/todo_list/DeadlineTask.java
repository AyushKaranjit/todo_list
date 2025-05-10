package com.example.todo_list;

import com.example.todo_list.exception.InvalidTaskInputException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DeadlineTask extends Task {
    private LocalTime dueTime;

    public DeadlineTask(String description, LocalDate reminderDate, LocalTime dueTime) {
        super(description);
        if (reminderDate == null && dueTime != null) {
            throw new InvalidTaskInputException("Cannot set due time without a reminder date.");
        }
        setReminderDate(reminderDate);
        this.dueTime = dueTime;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalTime dueTime) {
        if (dueTime != null && getReminderDate() == null) {
            throw new InvalidTaskInputException("Due time cannot be set without a reminder date.");
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