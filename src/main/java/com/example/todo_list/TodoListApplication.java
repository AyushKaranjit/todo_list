package com.example.todo_list;

import com.example.todo_list.exception.AppInitializationException;
import com.example.todo_list.util.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.example.todo_list.util.DatabaseManager;

import java.io.IOException;
import java.net.URL;

// Main application class for the To-Do List.
public class TodoListApplication extends Application {
    private TodoListController controller;

    // Starts the JavaFX application, setting up the primary stage and scene.
    @Override
    public void start(Stage stage) {
        try {
            // Start H2 Console
            DatabaseManager.startH2Console();
            
            FXMLLoader fxmlLoader = new FXMLLoader(TodoListApplication.class.getResource("/com/example/todo_list/todo-list-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 550, 500);
            
            URL cssUrl = TodoListApplication.class.getResource("/com/example/todo_list/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("Warning: Could not load styles.css");
            }
            
            controller = fxmlLoader.getController();
            
            stage.setTitle("To-Do List");
            stage.setScene(scene);
            stage.show();
            
            // Add window close handler
            stage.setOnCloseRequest((WindowEvent event) -> {
                if (controller != null) {
                    controller.cleanup();
                }
            });
        } catch (IOException e) {
            String errorMessage = "Failed to initialize the application UI. Please check application resources.";
            AlertUtil.showAlert("Application Error", "Fatal Error: " + errorMessage + "\nDetails: " + e.getMessage());
            throw new AppInitializationException(errorMessage, e);
        }
    }

    @Override
    public void stop() {
        // Stop H2 Console when application closes
        DatabaseManager.stopH2Console();
    }

    // Main method to launch the application.
    public static void main(String[] args) {
        launch();
    }
} 