module com.example.todo_list {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.todo_list to javafx.fxml;
    exports com.example.todo_list;
}