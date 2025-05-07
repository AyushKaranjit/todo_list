package com.example.todo_list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;

public class HelloController {

    @FXML
    private TextField taskInput;

    @FXML
    private Button addTaskButton;

    @FXML
    private ListView<String> taskListView;

    @FXML
    private DatePicker reminderDatePicker;

    @FXML
    private Button setReminderButton;

    @FXML
    private Button markCompleteButton;

    @FXML
    private Button deleteTaskButton;

    private ObservableList<String> tasks;

    @FXML
    public void initialize() {
        tasks = FXCollections.observableArrayList();
        taskListView.setItems(tasks);

        // Listener to enable/disable buttons based on selection
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean taskSelected = newValue != null;
            setReminderButton.setDisable(!taskSelected);
            markCompleteButton.setDisable(!taskSelected);
            deleteTaskButton.setDisable(!taskSelected);
            reminderDatePicker.setValue(null); // Clear date picker when selection changes
        });
    }

    @FXML
    protected void handleAddTask() {
        String taskDescription = taskInput.getText().trim();
        if (!taskDescription.isEmpty()) {
            tasks.add(taskDescription);
            taskInput.clear();
        } else {
            showAlert("Input Error", "Task description cannot be empty.");
        }
    }

    @FXML
    protected void handleSetReminder() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();
        LocalDate reminderDate = reminderDatePicker.getValue();

        if (selectedTask != null && reminderDate != null) {
            // In a real application, you would store this reminder information.
            // For now, we'll just show an alert.
            showAlert("Reminder Set", "Reminder for \"" + selectedTask + "\" set to " + reminderDate.toString());
            // You might want to update the task display to show it has a reminder
            // e.g., tasks.set(taskListView.getSelectionModel().getSelectedIndex(), selectedTask + " (Reminder: " + reminderDate.toString() + ")");
        } else if (selectedTask == null) {
            showAlert("Selection Error", "Please select a task to set a reminder.");
        } else {
            showAlert("Input Error", "Please select a date for the reminder.");
        }
    }

    @FXML
    protected void handleMarkComplete() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // In a real app, you'd update the task's status.
            // For this example, we'll prepend "[DONE]" and move it or remove it.
            // For simplicity, let's just remove it for now.
            int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
            // tasks.set(selectedIndex, "[DONE] " + selectedTask);
            // Or, more simply for this example:
            tasks.remove(selectedTask);
            showAlert("Task Complete", "\"" + selectedTask + "\" marked as complete and removed.");
        } else {
            showAlert("Selection Error", "Please select a task to mark as complete.");
        }
    }

    @FXML
    protected void handleDeleteTask() {
        String selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            showAlert("Task Deleted", "\"" + selectedTask + "\" has been deleted.");
        } else {
            showAlert("Selection Error", "Please select a task to delete.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
