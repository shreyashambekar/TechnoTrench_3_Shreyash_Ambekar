package com.vinnorman.taskmanagerapp;

public class Task {
    private String title;
    private boolean isCompleted;
    public Task() {

    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Task(String title) {
        this.title = title;
    }
}
