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
import javafx.collections.ListChangeListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.example.todo_list.util.NotificationUtil;
import com.example.todo_list.util.AlertUtil;
import com.example.todo_list.util.DateTimeUtil;
import com.example.todo_list.util.DatabaseManager;
import com.example.todo_list.exception.InvalidTaskInputException;
import com.example.todo_list.exception.DataPersistenceException;

// Controller class for the main view of the To-Do List application.
public class HelloController {

    // FXML UI Elements
    @FXML
    private TextField taskInput;
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
    private DatePicker reminderDatePicker;

    @FXML
    private Button addTaskButton;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private Button setReminderButton;
    @FXML
    private Button markCompleteButton;
    @FXML
    private Button deleteTaskButton;
    @FXML
    private Button updateTaskButton;
    @FXML
    private TextField searchField;

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label pendingTasksLabel;

    // Task list data
    private ObservableList<Task> masterTasksList;
    private FilteredList<Task> filteredTasksList;

    // Constants for task types
    private static final String SIMPLE_TASK = "Simple";
    private static final String DETAILED_TASK = "Detailed";
    private static final String DEADLINE_TASK = "Deadline";

    // Constants for CSS style classes
    private static final String STYLE_CLASS_TASK_COMPLETED = "task-completed";
    private static final String STYLE_CLASS_TASK_DUE = "task-due";
    private static final String STYLE_CLASS_TASK_OVERDUE = "task-overdue";

    // Initializes the controller class. This method is automatically called
    // after the fxml file has been loaded.
    @FXML
    public void initialize() {
        // Load tasks from database
        try {
            masterTasksList = DatabaseManager.loadTasks();
        } catch (DataPersistenceException e) {
            AlertUtil.showAlert("Database Error", "Failed to load tasks: " + e.getMessage());
            masterTasksList = FXCollections.observableArrayList();
        }
        
        filteredTasksList = new FilteredList<>(masterTasksList, p -> true);

        taskListView.setItems(filteredTasksList);

        taskTypeComboBox.setItems(FXCollections.observableArrayList(SIMPLE_TASK, DETAILED_TASK, DEADLINE_TASK));
        taskTypeComboBox.setValue(SIMPLE_TASK);

        // Listener to update UI fields based on selected task type.
        taskTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTaskSpecificFieldVisibility(newVal);
        });
        updateTaskSpecificFieldVisibility(SIMPLE_TASK);

        // Listener to filter tasks based on text entered in the search field.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTasksList.setPredicate(task -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (task.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                }

                if (task instanceof DetailedTask) {
                    DetailedTask detailedTask = (DetailedTask) task;
                    if (detailedTask.getDetails() != null && detailedTask.getDetails().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }
                return false;
            });
        });

        // Sets a custom cell factory for the task ListView to control how tasks are displayed.
        // This includes applying different styles based on task status (completed, due, overdue).
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                getStyleClass().removeAll(STYLE_CLASS_TASK_COMPLETED, STYLE_CLASS_TASK_DUE, STYLE_CLASS_TASK_OVERDUE);
                setText(null); 

                if (empty || task == null) {
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
                    } 
                }
            }
        });

        // Listener to update input fields and button states when a task is selected in the ListView.
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean taskSelected = newValue != null;
            setReminderButton.setDisable(!taskSelected);
            markCompleteButton.setDisable(!taskSelected);
            deleteTaskButton.setDisable(!taskSelected);
            updateTaskButton.setDisable(!taskSelected || (newValue != null && newValue.isCompleted()));

            if (taskSelected) {
                taskInput.setText(newValue.getDescription());
                reminderDatePicker.setValue(newValue.getReminderDate());
                taskTypeComboBox.setValue(newValue.getType()); 
                if (newValue instanceof DetailedTask) {
                    detailsInput.setText(((DetailedTask) newValue).getDetails());
                } else {
                    detailsInput.clear();
                }
                if (newValue instanceof DeadlineTask) { 
                    dueTimeInput.setText(DateTimeUtil.formatTime(((DeadlineTask) newValue).getDueTime()));
                } else {
                    dueTimeInput.clear();
                }
            } else {
                taskInput.clear();
                reminderDatePicker.setValue(null);
                taskTypeComboBox.setValue(SIMPLE_TASK);
                detailsInput.clear();
                dueTimeInput.clear();
            }
            updateTaskSpecificFieldVisibility(taskTypeComboBox.getValue());
        });

        // Listener to update task statistics when the master list of tasks changes.
        masterTasksList.addListener((ListChangeListener<Task>) c -> {
            updateTaskStatistics();
            // Save tasks to database whenever the list changes
            saveTasks();
        });

        updateTaskStatistics(); 
    }

    // Updates the visibility of task-specific input fields (details, due time)
    // based on the selected task type.
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

    // Updates the labels displaying task statistics (total, completed, pending).
    private void updateTaskStatistics() {
        long total = masterTasksList.size();
        long completed = masterTasksList.stream().filter(Task::isCompleted).count();
        long pending = total - completed;

        totalTasksLabel.setText("Total: " + total);
        completedTasksLabel.setText("Completed: " + completed);
        pendingTasksLabel.setText("Pending: " + pending);
    }
    
    // Saves tasks to database
    private void saveTasks() {
        try {
            DatabaseManager.saveTasks(masterTasksList);
        } catch (DataPersistenceException e) {
            AlertUtil.showAlert("Database Error", "Failed to save tasks: " + e.getMessage());
        }
    }

    // Handles the action of adding a new task.
    @FXML
    protected void handleAddTask() {
        String description = taskInput.getText().trim();
        if (description.isEmpty()) {
            AlertUtil.showAlert("Input Error", "Task description cannot be empty.");
            return;
        }

        LocalDate reminderForTask = reminderDatePicker.getValue();
        if (reminderForTask != null && reminderForTask.isBefore(LocalDate.now())) {
            AlertUtil.showAlert("Input Error", "Reminder date cannot be in the past.");
            return;
        }

        String selectedType = taskTypeComboBox.getValue();
        Task newTask = null;
        try {
            switch (selectedType) {
                case SIMPLE_TASK:
                    newTask = new SimpleTask(description);
                    if (reminderForTask != null) {
                        newTask.setReminderDate(reminderForTask);
                    }
                    break;
                case DETAILED_TASK:
                    String details = detailsInput.getText().trim();
                    newTask = new DetailedTask(description, details);
                    if (reminderForTask != null) { 
                        newTask.setReminderDate(reminderForTask);
                    }
                    break;
                case DEADLINE_TASK:
                    LocalTime dueTime = null;
                    String dueTimeText = dueTimeInput.getText().trim();
                    dueTime = DateTimeUtil.parseTime(dueTimeText);

                    if (reminderForTask != null && reminderForTask.isEqual(LocalDate.now()) && 
                        dueTime != null && dueTime.isBefore(LocalTime.now())) {
                        AlertUtil.showAlert("Input Error", "Due time for a task due today cannot be in the past.");
                        return;
                    }

                    newTask = new DeadlineTask(description, reminderForTask, dueTime);
                    break;
                default:
                    AlertUtil.showAlert("Type Error", "Unknown task type selected.");
                    return;
            }
        } catch (InvalidTaskInputException e) {
            AlertUtil.showAlert("Input Error", e.getMessage());
            return;
        } catch (DateTimeParseException e) {
            AlertUtil.showAlert("Input Error", "Invalid due time format. Please use HH:mm.");
            return;
        } catch (IllegalArgumentException e) {
            AlertUtil.showAlert("Input Error", "An unexpected input error occurred: " + e.getMessage());
            return;
        }

        if (newTask != null) {
            masterTasksList.add(newTask);
            clearInputFields();
            updateTaskStatistics(); 

            if (newTask instanceof DeadlineTask) {
                DeadlineTask deadlineTask = (DeadlineTask) newTask;
                if (deadlineTask.getReminderDate() != null && deadlineTask.getReminderDate().isBefore(LocalDate.now())) {
                    if (deadlineTask.getDueTime() != null && deadlineTask.getReminderDate().isEqual(LocalDate.now()) && deadlineTask.getDueTime().isBefore(LocalTime.now())) {
                        NotificationUtil.showTaskNotification("Task Overdue", "Deadline task \"" + deadlineTask.getDescription() + "\" was added already overdue.");
                    } else if (deadlineTask.getDueTime() == null) { 
                         NotificationUtil.showTaskNotification("Task Past Reminder", "Deadline task \"" + deadlineTask.getDescription() + "\" was added past its due date.");
                    }
                } else if (deadlineTask.getReminderDate() != null && deadlineTask.getReminderDate().isEqual(LocalDate.now()) && deadlineTask.getDueTime() != null && deadlineTask.getDueTime().isBefore(LocalTime.now())) {
                     NotificationUtil.showTaskNotification("Task Overdue", "Deadline task \"" + deadlineTask.getDescription() + "\" is due today and already past time.");
                }
            } else if (newTask.getReminderDate() != null && newTask.getReminderDate().isBefore(LocalDate.now())) {
                NotificationUtil.showTaskNotification("Task Past Reminder", "Task \"" + newTask.getDescription() + "\" was added with a reminder date in the past.");
            }
        }
    }

    // Clears all input fields and resets the task type ComboBox.
    private void clearInputFields() {
        taskInput.clear();
        detailsInput.clear();
        dueTimeInput.clear();
        reminderDatePicker.setValue(null);
        taskTypeComboBox.setValue(SIMPLE_TASK);
        updateTaskSpecificFieldVisibility(SIMPLE_TASK);
    }

    // Handles the action of setting or updating a reminder for the selected task.
    @FXML
    protected void handleSetReminder() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        LocalDate reminderDate = reminderDatePicker.getValue();

        if (selectedTask == null) {
            AlertUtil.showAlert("Selection Error", "Please select a task to set a reminder.");
            return;
        }
        if (reminderDate == null && !(selectedTask instanceof DeadlineTask && ((DeadlineTask)selectedTask).getDueTime() != null)){
             AlertUtil.showAlert("Input Error", "Please select a date for the reminder.");
            return;
        }
        if (selectedTask instanceof DeadlineTask && reminderDate == null && ((DeadlineTask) selectedTask).getDueTime() != null) {
            AlertUtil.showAlert("Input Error", "Cannot clear reminder date for a Deadline Task that has a due time. Clear due time first or set a new date.");
            reminderDatePicker.setValue(selectedTask.getReminderDate());
            return;
        }

        selectedTask.setReminderDate(reminderDate);
        
        refreshListView();
        saveTasks();
        AlertUtil.showAlert("Reminder Set", "Reminder for \"" + selectedTask.getDescription() + "\" set to " + (reminderDate != null ? reminderDate.toString() : "cleared") + ".");
    }

    // Handles the action of marking the selected task as complete.
    @FXML
    protected void handleMarkComplete() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            if (!selectedTask.isCompleted()) { 
                selectedTask.setCompleted(true);
                refreshListView();
                updateTaskStatistics(); 
                saveTasks();
                NotificationUtil.showTaskNotification("Task Completed", "\"" + selectedTask.getDescription() + "\" is complete!");
            }
            AlertUtil.showAlert("Task Complete", "\"" + selectedTask.getDescription() + "\" marked as complete.");
        } else {
            AlertUtil.showAlert("Selection Error", "Please select a task to mark as complete.");
        }
    }

    // Handles the action of deleting the selected task.
    @FXML
    protected void handleDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            masterTasksList.remove(selectedTask);
            updateTaskStatistics(); 
            AlertUtil.showAlert("Task Deleted", "\"" + selectedTask.getDescription() + "\" has been deleted.");
        } else {
            AlertUtil.showAlert("Selection Error", "Please select a task to delete.");
        }
    }

    // Handles the action of updating the selected task with new information from the input fields.
    @FXML
    protected void handleUpdateTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            AlertUtil.showAlert("No Task Selected", "Please select a task to update.");
            return;
        }

        if (selectedTask.isCompleted()) {
            AlertUtil.showAlert("Task Completed", "Completed tasks cannot be updated.");
            return;
        }

        String newDescription = taskInput.getText().trim();
        LocalDate newReminderDate = reminderDatePicker.getValue();

        if (newReminderDate != null && newReminderDate.isBefore(LocalDate.now())) {
            boolean reminderDateChanged = (selectedTask.getReminderDate() == null && newReminderDate != null) || 
                                          (selectedTask.getReminderDate() != null && !selectedTask.getReminderDate().equals(newReminderDate));
            if (reminderDateChanged && newReminderDate.isBefore(LocalDate.now())) {
                 AlertUtil.showAlert("Input Error", "Reminder date cannot be set to a date in the past.");
                 return;
            }
        }

        String newType = taskTypeComboBox.getValue();
        String newDetails = detailsInput.getText().trim();
        String newDueTimeText = dueTimeInput.getText().trim();

        if (newDescription.isEmpty()) {
            AlertUtil.showAlert("Input Error", "Task description cannot be empty.");
            return;
        }

        try {
            LocalTime newDueTime = DateTimeUtil.parseTime(newDueTimeText);

            if (newType.equals(DEADLINE_TASK)) {
                if (newReminderDate != null && newReminderDate.isEqual(LocalDate.now()) &&
                    newDueTime != null && newDueTime.isBefore(LocalTime.now())) {
                    AlertUtil.showAlert("Input Error", "Due time for a task due today cannot be in the past.");
                    return;
                }
            }

            if (!selectedTask.getType().equals(newType)) {
           
                Task newTask;
                switch (newType) {
                    case SIMPLE_TASK:
                        newTask = new SimpleTask(newDescription);
                        break;
                    case DETAILED_TASK:
                        newTask = new DetailedTask(newDescription, newDetails);
                        break;
                    case DEADLINE_TASK:
                        newTask = new DeadlineTask(newDescription, newReminderDate, newDueTime);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid task type: " + newType);
                }
        
                newTask.setCompleted(selectedTask.isCompleted()); 
                if (!(newTask instanceof DeadlineTask)) { 
                     if(newReminderDate != null) newTask.setReminderDate(newReminderDate);
                     else newTask.setReminderDate(selectedTask.getReminderDate());
                } else if (newReminderDate != null){
                    newTask.setReminderDate(newReminderDate);
                }
             

                masterTasksList.remove(selectedTask);
                masterTasksList.add(newTask);
                taskListView.getSelectionModel().select(newTask); 
            } else {
                
                selectedTask.setDescription(newDescription);
                selectedTask.setReminderDate(newReminderDate); 

                if (selectedTask instanceof DetailedTask) {
                    ((DetailedTask) selectedTask).setDetails(newDetails);
                }
                if (selectedTask instanceof DeadlineTask) {
                    
                    if (newReminderDate == null && ((DeadlineTask) selectedTask).getDueTime() != null) {
                        ((DeadlineTask) selectedTask).setDueTime(null); 
                        dueTimeInput.clear();
                        AlertUtil.showAlert("Due Time Cleared", "Reminder date was cleared, so due time was also cleared for the deadline task.");
                    }
                    ((DeadlineTask) selectedTask).setDueTime(newDueTime);
                }
            }

            refreshListView();
            updateTaskStatistics();
            saveTasks();
            AlertUtil.showAlert("Task Updated", "Task '" + newDescription + "' has been updated.");

        } catch (InvalidTaskInputException e) {
            AlertUtil.showAlert("Input Error", e.getMessage());
        } catch (DateTimeParseException e) {
            AlertUtil.showAlert("Input Error", "Invalid due time format. Please use HH:mm.");
        } catch (IllegalArgumentException e) {
            AlertUtil.showAlert("Update Error", "An unexpected error occurred during update: " + e.getMessage());
        }
    }
    
    // Refreshes the ListView to reflect any changes to the tasks.
    private void refreshListView() {
        taskListView.refresh();
    }
}
