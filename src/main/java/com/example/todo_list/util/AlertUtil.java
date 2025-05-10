package com.example.todo_list.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtil {

    // Private constructor to prevent instantiation of utility class
    private AlertUtil() {}

    /**
     * Shows a standard JavaFX Information Alert with the given title and content.
     * The alert dialog will wait for the user to close it before returning.
     * @param title The title of the alert dialog window.
     * @param content The main message content of the alert.
     */
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text for simpler alerts
        alert.setContentText(content);
        alert.showAndWait();
    }
} 