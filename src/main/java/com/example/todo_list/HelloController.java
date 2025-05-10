package com.example.todo_list;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
// import javafx.util.Callback; // No longer explicitly needed for simple toString cell factory

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HelloController {

    @FXML
    private TextField taskInput; // Remains for description

    @FXML
    private ComboBox<String> taskTypeComboBox;

    @FXML
    private Label detailsLabel;

    @FXML
    private TextField detailsInput;

    @FXML
    private Label dueTimeLabel;

    @FXML
    private TextField dueTimeInput;

    @FXML
    private Button addTaskButton;

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private DatePicker reminderDatePicker;

    @FXML
    private Button setReminderButton;

    @FXML
    private Button markCompleteButton;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private TextField searchField;

    private ObservableList<Task> masterTasksList;
    private FilteredList<Task> filteredTasksList;

    private static final String SIMPLE_TASK = "Simple";
    private static final String DETAILED_TASK = "Detailed";
    private static final String DEADLINE_TASK = "Deadline";
    private static final String STYLE_CLASS_TASK_COMPLETED = "task-completed";
    private static final String STYLE_CLASS_TASK_DUE = "task-due";
    private static final String STYLE_CLASS_TASK_OVERDUE = "task-overdue";

    @FXML
    public void initialize() {
        masterTasksList = FXCollections.observableArrayList();
        filteredTasksList = new FilteredList<>(masterTasksList, p -> true);

        taskListView.setItems(filteredTasksList);

        taskTypeComboBox.setItems(FXCollections.observableArrayList(SIMPLE_TASK, DETAILED_TASK, DEADLINE_TASK));
        taskTypeComboBox.setValue(SIMPLE_TASK); // Default to Simple Task

        // Listener to manage visibility of type-specific fields
        taskTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTaskSpecificFieldVisibility(newVal);
        });
        updateTaskSpecificFieldVisibility(SIMPLE_TASK); // Initial setup

        // Search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTasksList.setPredicate(task -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all tasks if search field is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();
                // Search in task description
                if (task.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                }
                // Optionally, search in details for DetailedTask
                if (task instanceof DetailedTask) {
                    DetailedTask detailedTask = (DetailedTask) task;
                    if (detailedTask.getDetails() != null && detailedTask.getDetails().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }
                return false; // Does not match
            });
        });

        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                // Always clear old styles first
                getStyleClass().removeAll(STYLE_CLASS_TASK_COMPLETED, STYLE_CLASS_TASK_DUE, STYLE_CLASS_TASK_OVERDUE);
                setText(null); // Clear text first
                // setGraphic(null); // If you were using graphics

                if (empty || task == null) {
                    // No text or style for empty cells
                } else {
                    setText(task.toString());
                    if (task.isCompleted()) {
                        getStyleClass().add(STYLE_CLASS_TASK_COMPLETED);
                    } else if (task.getReminderDate() != null && task.getReminderDate().isBefore(LocalDate.now().plusDays(1))) {
                        if (task instanceof DeadlineTask && ((DeadlineTask) task).getDueTime() != null && 
                            task.getReminderDate().isEqual(LocalDate.now()) && 
                            ((DeadlineTask) task).getDueTime().isBefore(LocalTime.now())) {
                            getStyleClass().add(STYLE_CLASS_TASK_OVERDUE);
                        } else {
                            getStyleClass().add(STYLE_CLASS_TASK_DUE);
                        }
                    } else {
                        // No specific style class for normal, non-due, non-completed tasks
                        // The default .list-cell styling from CSS will apply
                    }
                }
            }
        });

        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean taskSelected = newValue != null;
            setReminderButton.setDisable(!taskSelected);
            markCompleteButton.setDisable(!taskSelected);
            deleteTaskButton.setDisable(!taskSelected);

            if (taskSelected) {
                reminderDatePicker.setValue(newValue.getReminderDate());
                // Populate type-specific fields if editing is intended
                taskTypeComboBox.setValue(newValue.getType()); 
                if (newValue instanceof DetailedTask) {
                    detailsInput.setText(((DetailedTask) newValue).getDetails());
                } else {
                    detailsInput.clear();
                }
                if (newValue instanceof DeadlineTask && ((DeadlineTask) newValue).getDueTime() != null) {
                    dueTimeInput.setText(((DeadlineTask) newValue).getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                } else {
                    dueTimeInput.clear();
                }
            } else {
                reminderDatePicker.setValue(null);
                taskTypeComboBox.setValue(SIMPLE_TASK);
                detailsInput.clear();
                dueTimeInput.clear();
            }
            updateTaskSpecificFieldVisibility(taskTypeComboBox.getValue());
        });
    }

    private void updateTaskSpecificFieldVisibility(String taskType) {
        boolean isDetailed = DETAILED_TASK.equals(taskType);
        boolean isDeadline = DEADLINE_TASK.equals(taskType);

        detailsLabel.setVisible(isDetailed);
        detailsLabel.setManaged(isDetailed);
        detailsInput.setVisible(isDetailed);
        detailsInput.setManaged(isDetailed);

        dueTimeLabel.setVisible(isDeadline);
        dueTimeLabel.setManaged(isDeadline);
        dueTimeInput.setVisible(isDeadline);
        dueTimeInput.setManaged(isDeadline);
    }

    @FXML
    protected void handleAddTask() {
        String description = taskInput.getText().trim();
        if (description.isEmpty()) {
            showAlert("Input Error", "Task description cannot be empty.");
            return;
        }

        String selectedType = taskTypeComboBox.getValue();
        Task newTask = null;

        try {
            switch (selectedType) {
                case SIMPLE_TASK:
                    newTask = new SimpleTask(description);
                    break;
                case DETAILED_TASK:
                    String details = detailsInput.getText().trim();
                    newTask = new DetailedTask(description, details);
                    break;
                case DEADLINE_TASK:
                    LocalDate reminderForDeadline = reminderDatePicker.getValue(); // Deadline tasks should ideally have a date
                    LocalTime dueTime = null;
                    String dueTimeText = dueTimeInput.getText().trim();
                    if (!dueTimeText.isEmpty()) {
                        dueTime = LocalTime.parse(dueTimeText, DateTimeFormatter.ofPattern("HH:mm"));
                    }
                    // We decided DeadlineTask constructor handles reminderDate null check with dueTime.
                    newTask = new DeadlineTask(description, reminderForDeadline, dueTime);
                    // if (reminderForDeadline == null && dueTime != null) {
                    //     showAlert("Input Error", "A due time for a Deadline Task requires a reminder date to be set.");
                    //     return;
                    // }
                    break;
                default:
                    showAlert("Type Error", "Unknown task type selected.");
                    return;
            }
        } catch (DateTimeParseException e) {
            showAlert("Input Error", "Invalid due time format. Please use HH:mm.");
            return;
        } catch (IllegalArgumentException e) {
            showAlert("Input Error", e.getMessage());
            return;
        }

        if (newTask != null) {
            masterTasksList.add(newTask);
            clearInputFields();
        }
    }

    private void clearInputFields() {
        taskInput.clear();
        detailsInput.clear();
        dueTimeInput.clear();
        // reminderDatePicker.setValue(null); // Keep reminder date for next task if desired, or clear
        taskTypeComboBox.setValue(SIMPLE_TASK); // Reset to default type
        updateTaskSpecificFieldVisibility(SIMPLE_TASK);
    }

    @FXML
    protected void handleSetReminder() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        LocalDate reminderDate = reminderDatePicker.getValue();

        if (selectedTask == null) {
            showAlert("Selection Error", "Please select a task to set a reminder.");
            return;
        }
        if (reminderDate == null && !(selectedTask instanceof DeadlineTask && ((DeadlineTask)selectedTask).getDueTime() != null)){
             showAlert("Input Error", "Please select a date for the reminder.");
            return;
        }
        // For DeadlineTask, if user is clearing reminder date but due time exists, it might be an issue based on constructor logic.
        // Current DeadlineTask.setDueTime() checks if reminderDate is null.
        // Let's ensure that if date is cleared, due time is also cleared for DeadlineTask to avoid inconsistency or prompt user.
        if (selectedTask instanceof DeadlineTask && reminderDate == null && ((DeadlineTask) selectedTask).getDueTime() != null) {
             // Option 1: Clear due time as well
            // ((DeadlineTask) selectedTask).setDueTime(null); 
            // showAlert("Reminder Cleared", "Reminder and associated due time for '" + selectedTask.getDescription() + "' have been cleared.");
            // Option 2: Prevent clearing date if due time exists, or prompt.
            showAlert("Input Error", "Cannot clear reminder date for a Deadline Task that has a due time. Clear due time first or set a new date.");
            reminderDatePicker.setValue(selectedTask.getReminderDate()); // Revert date picker
            return;
        }

        selectedTask.setReminderDate(reminderDate);
        
        // If it's a DeadlineTask and user is setting a date, ensure dueTime input is available or handled
        if (selectedTask instanceof DeadlineTask) {
            // If a due time is already set, ensure it's compatible or allow user to update it.
            // For now, setting the date is enough. The due time is set during task creation or could be edited via a dedicated UI.
            // If reminderDate is set to null, the DeadlineTask's setDueTime(null) might be implicitly needed if it had one.
            // However, our check above handles the case of clearing date when dueTime exists.
        }

        refreshListView();
        showAlert("Reminder Set", "Reminder for \"" + selectedTask.getDescription() + "\" set to " + (reminderDate != null ? reminderDate.toString() : "cleared") + ".");
    }

    @FXML
    protected void handleMarkComplete() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(true);
            refreshListView();
            showAlert("Task Complete", "\"" + selectedTask.getDescription() + "\" marked as complete.");
        } else {
            showAlert("Selection Error", "Please select a task to mark as complete.");
        }
    }

    @FXML
    protected void handleDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            masterTasksList.remove(selectedTask);
            showAlert("Task Deleted", "\"" + selectedTask.getDescription() + "\" has been deleted.");
        } else {
            showAlert("Selection Error", "Please select a task to delete.");
        }
    }
    
    private void refreshListView() {
        taskListView.refresh();
        // int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
        // taskListView.refresh();
        // if (selectedIndex != -1) taskListView.getSelectionModel().select(selectedIndex);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
