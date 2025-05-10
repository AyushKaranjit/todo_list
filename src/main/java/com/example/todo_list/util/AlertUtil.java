package com.example.todo_list.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// Utility class for displaying JavaFX alerts.
public class AlertUtil {

    // Private constructor to prevent instantiation of utility class
    private AlertUtil() {}

    // Displays an information alert dialog.
    public static void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 