package com.example.todo_list;

public class SimpleTask extends Task {

    public SimpleTask(String description) {
        super(description);
    }

    @Override
    public String getType() {
        return "Simple";
    }
} 