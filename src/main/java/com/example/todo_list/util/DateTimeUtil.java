package com.example.todo_list.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for parsing and formatting {@link LocalTime} objects.
 * Uses a predefined HH:mm format.
 */
public class DateTimeUtil {

    /**
     * A pre-defined formatter for time in "HH:mm" pattern (e.g., 14:30).
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Private constructor to prevent instantiation of utility class
    private DateTimeUtil() {}

    /**
     * Parses a string representing a time into a {@link LocalTime} object.
     * The expected format is "HH:mm".
     * @param timeString The string to parse. If null or empty, returns null.
     * @return The parsed {@link LocalTime}, or null if the input string is null or empty.
     * @throws DateTimeParseException if the timeString cannot be parsed (e.g., invalid format).
     */
    public static LocalTime parseTime(String timeString) throws DateTimeParseException {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeString.trim(), TIME_FORMATTER);
    }

    /**
     * Formats a {@link LocalTime} object into a string representation.
     * Uses the "HH:mm" format.
     * @param time The {@link LocalTime} to format. If null, returns an empty string.
     * @return The formatted time string (e.g., "14:30"), or an empty string if the input time is null.
     */
    public static String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMATTER);
    }
} 