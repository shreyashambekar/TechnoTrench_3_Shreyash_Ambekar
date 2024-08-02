package com.vinnorman.taskmanagerapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String KEY_TASK_COUNT = "TaskCount";
    private LinearLayout tasksContainer;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasksContainer = findViewById(R.id.tasksContainer);
        TextView saveButton = findViewById(R.id.saveButton);

        taskList = new ArrayList<>();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        loadTasksFromPreferences();
        displayTask();
    }

    private void displayTask() {
        for (Task task : taskList) {
            createTaskView(task);
        }
    }

    private void loadTasksFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int taskCount = sharedPreferences.getInt(KEY_TASK_COUNT, 0);

        for (int i = 0; i < taskCount; i++) {
            String title = sharedPreferences.getString("task_title_" + i, "");
            boolean isCompleted = sharedPreferences.getBoolean("task_completed_" + i, false);

            Task task = new Task();
            task.setTitle(title);
            task.setCompleted(isCompleted);

            taskList.add(task);
        }
    }

    private void saveTask() {
        EditText titleEditText = findViewById(R.id.titleEditText);

        String title = titleEditText.getText().toString();

        if (!title.isEmpty() ) {
            Task task = new Task();
            task.setTitle(title);

            taskList.add(task);
            saveTasksToPreferences();

            createTaskView(task);
            clearInputFields();
        }
    }

    private void clearInputFields() {
        EditText titleEditText = findViewById(R.id.titleEditText);

        titleEditText.getText().clear();
    }

    private void createTaskView(final Task task) {
        View taskView = getLayoutInflater().inflate(R.layout.task_item, null);
        TextView titleTextView = taskView.findViewById(R.id.titleTextView);
        CheckBox completedCheckBox = taskView.findViewById(R.id.completedCheckBox);
        TextView updateButton = taskView.findViewById(R.id.Button_upadate_task);

        titleTextView.setText(task.getTitle());
        completedCheckBox.setChecked(task.isCompleted());

        completedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setCompleted(isChecked);
                saveTasksToPreferences();
            }
        });

        taskView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(task);
                return true;
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog(task);
            }
        });

        tasksContainer.addView(taskView);
    }

    private void showDeleteDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this task.");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTaskAndRefresh(task);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showUpdateDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Task");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
        builder.setView(dialogView);

        final EditText titleEditText = dialogView.findViewById(R.id.titleEditText);

        titleEditText.setText(task.getTitle());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedTitle = titleEditText.getText().toString();

                if (!updatedTitle.isEmpty() ) {
                    task.setTitle(updatedTitle);
                    saveTasksToPreferences();
                    refreshTaskViews();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteTaskAndRefresh(Task task) {
        taskList.remove(task);
        saveTasksToPreferences();
        refreshTaskViews();
    }

    private void refreshTaskViews() {
        tasksContainer.removeAllViews();
        displayTask();
    }

    private void saveTasksToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_TASK_COUNT, taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            editor.putString("task_title_" + i, task.getTitle());
            editor.putBoolean("task_completed_" + i, task.isCompleted());
        }
        editor.apply();
    }
}
