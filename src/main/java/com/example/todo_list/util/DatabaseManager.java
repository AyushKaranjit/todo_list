package com.example.todo_list.util;

import com.example.todo_list.DeadlineTask;
import com.example.todo_list.DetailedTask;
import com.example.todo_list.SimpleTask;
import com.example.todo_list.Task;
import com.example.todo_list.exception.DataPersistenceException;
import org.h2.tools.Server;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


// Manages database connections and operations for the to-do list application.
// Uses H2 database for persistence.

public class DatabaseManager {
    private static final String DB_DIRECTORY = System.getProperty("user.home") + File.separator + ".todo_list";
    private static final String DB_URL = "jdbc:h2:file:" + DB_DIRECTORY + File.separator + "todo_db;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";
    private static Server server;
    
    private static final String CREATE_TASKS_TABLE = 
            "CREATE TABLE IF NOT EXISTS tasks (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "type VARCHAR(20) NOT NULL, " +
            "description VARCHAR(255) NOT NULL, " +
            "details VARCHAR(1000), " +
            "reminder_date DATE, " +
            "due_time TIME, " +
            "completed BOOLEAN NOT NULL DEFAULT FALSE" +
            ")";
    
    static {
        // Initialize database on class load
        try {
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize the database: " + e.getMessage());
        }
    }
    
    public static void startH2Console() {
        try {
            if (server == null) {
                server = Server.createWebServer("-webPort", "8082").start();
                System.out.println("\n=== H2 Database Console ===");
                System.out.println("URL: http://localhost:8082");
                System.out.println("JDBC URL: " + DB_URL);
                System.out.println("Username: " + DB_USER);
                System.out.println("Password: " + DB_PASSWORD);
                System.out.println("========================\n");
                
                // Add shutdown hook to stop the H2 Console server when the JVM exits
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (server != null) {
                        server.stop();
                        System.out.println("H2 Console server stopped.");
                    }
                }));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start H2 Console: " + e.getMessage());
        }
    }
    
    public static void stopH2Console() {
        if (server != null) {
            server.stop();
            server = null;
            System.out.println("H2 Console server stopped.");
        }
    }
    
    // Establishes and returns a connection to the database.
    
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    
    // Saves all tasks to the database.
    // First clears all existing tasks and then inserts the current list.
    
    public static void saveTasks(List<Task> tasks) throws DataPersistenceException {
        try (Connection conn = getConnection()) {
            // Begin transaction
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO tasks (type, description, details, reminder_date, due_time, completed) " +
                     "VALUES (?, ?, ?, ?, ?, ?)"
                 )) {
                
                // Clear existing data
                stmt.executeUpdate("DELETE FROM tasks");
                
                // Insert new data
                for (Task task : tasks) {
                    pstmt.setString(1, task.getType());
                    pstmt.setString(2, task.getDescription());
                    
                    if (task instanceof DetailedTask) {
                        pstmt.setString(3, ((DetailedTask) task).getDetails());
                    } else {
                        pstmt.setNull(3, Types.VARCHAR);
                    }
                    
                    if (task.getReminderDate() != null) {
                        pstmt.setDate(4, Date.valueOf(task.getReminderDate()));
                    } else {
                        pstmt.setNull(4, Types.DATE);
                    }
                    
                    if (task instanceof DeadlineTask && ((DeadlineTask) task).getDueTime() != null) {
                        pstmt.setTime(5, Time.valueOf(((DeadlineTask) task).getDueTime()));
                    } else {
                        pstmt.setNull(5, Types.TIME);
                    }
                    
                    pstmt.setBoolean(6, task.isCompleted());
                    
                    pstmt.executeUpdate();
                }
                
                // Commit transaction
                conn.commit();
            } catch (SQLException e) {
                // Roll back in case of error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataPersistenceException("Failed to save tasks to database: " + e.getMessage(), e);
        }
    }
    
    
    // Loads all tasks from the database.
    
    public static ObservableList<Task> loadTasks() throws DataPersistenceException {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {
            
            while (rs.next()) {
                String type = rs.getString("type");
                String description = rs.getString("description");
                String details = rs.getString("details");
                Date reminderDate = rs.getDate("reminder_date");
                Time dueTime = rs.getTime("due_time");
                boolean completed = rs.getBoolean("completed");
                
                Task task;
                switch (type) {
                    case "Simple":
                        task = new SimpleTask(description);
                        break;
                    case "Detailed":
                        task = new DetailedTask(description, details);
                        break;
                    case "Deadline":
                        LocalDate localReminderDate = reminderDate != null ? reminderDate.toLocalDate() : null;
                        LocalTime localDueTime = dueTime != null ? dueTime.toLocalTime() : null;
                        task = new DeadlineTask(description, localReminderDate, localDueTime);
                        break;
                    default:
                        // Skip unknown task types
                        continue;
                }
                
                if (reminderDate != null && !(task instanceof DeadlineTask)) {
                    task.setReminderDate(reminderDate.toLocalDate());
                }
                
                task.setCompleted(completed);
                tasks.add(task);
            }
            
        } catch (SQLException e) {
            throw new DataPersistenceException("Failed to load tasks from database: " + e.getMessage(), e);
        }
        
        return tasks;
    }
    
    // Initializes the database and creates necessary tables.
    private static void initializeDatabase() throws SQLException {
        // Create database directory if it doesn't exist
        File dbDir = new File(DB_DIRECTORY);
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new SQLException("Failed to create database directory: " + DB_DIRECTORY);
            }
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TASKS_TABLE);
        }
    }
} 