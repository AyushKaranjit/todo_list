package com.example.todo_list.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// Utility class for displaying notifications.
public final class NotificationUtil {

    // Private constructor to prevent instantiation.
    private NotificationUtil() {
        // Private constructor to prevent instantiation
    }

    // Shows a task-related notification.
    public static void showTaskNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Task Notification"); // Generic window title for all task notifications
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 