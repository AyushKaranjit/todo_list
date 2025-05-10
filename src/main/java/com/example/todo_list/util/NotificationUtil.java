package com.example.todo_list.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility class for showing task-related notifications to the user.
 * Currently uses simple JavaFX Alerts for notifications.
 */
public final class NotificationUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private NotificationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Displays a task-related notification as an Information Alert.
     * @param title The header text for the notification alert (e.g., "Task Completed", "Task Overdue").
     * @param message The main content/message of the notification.
     */
    public static void showTaskNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Task Notification"); // Generic window title for all task notifications
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 