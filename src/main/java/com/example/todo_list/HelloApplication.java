package com.example.todo_list;

import com.example.todo_list.exception.AppInitializationException;
import com.example.todo_list.util.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

// Main application class for the To-Do List.
public class HelloApplication extends Application {
    // Starts the JavaFX application, setting up the primary stage and scene.
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/todo_list/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 550, 500);
            
            URL cssUrl = HelloApplication.class.getResource("/com/example/todo_list/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("Warning: Could not load styles.css");
            }
            
            stage.setTitle("To-Do List");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            String errorMessage = "Failed to initialize the application UI. Please check application resources.";
            AlertUtil.showAlert("Application Error", "Fatal Error: " + errorMessage + "\nDetails: " + e.getMessage());
            throw new AppInitializationException(errorMessage, e);
        }
    }

    // Main method to launch the application.
    public static void main(String[] args) {
        launch();
    }
}
