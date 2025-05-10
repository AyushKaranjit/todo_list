package com.example.todo_list.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Utility class for date and time operations.
public class DateTimeUtil {

    // Formatter for time in HH:mm format.
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Private constructor to prevent instantiation of utility class.
    private DateTimeUtil() {}

    // Parses a time string into a LocalTime object.
    public static LocalTime parseTime(String timeString) throws DateTimeParseException {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeString.trim(), TIME_FORMATTER);
    }
    
    // Formats a LocalTime object into a time string.
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMATTER);
    }
} 