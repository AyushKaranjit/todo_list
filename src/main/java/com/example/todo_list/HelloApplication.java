package com.example.todo_list;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load custom fonts
        try {
            Font.loadFont(HelloApplication.class.getResourceAsStream("/com/example/todo_list/fonts/Inter-Regular.ttf"), 10); // Size 10 is arbitrary, font is loaded by family name
            Font.loadFont(HelloApplication.class.getResourceAsStream("/com/example/todo_list/fonts/Inter-Bold.ttf"), 10);
            // Add other weights/styles if needed (e.g., Inter-Italic.ttf)
            System.out.println("Attempted to load Inter fonts."); // For debugging
        } catch (Exception e) {
            System.err.println("Failed to load Inter fonts: " + e.getMessage());
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/todo_list/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 500);
        
        // Load and apply CSS
        URL cssUrl = HelloApplication.class.getResource("/com/example/todo_list/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("Warning: Could not load styles.css");
        }
        
        stage.setTitle("To-Do List");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
