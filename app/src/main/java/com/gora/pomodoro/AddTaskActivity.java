package com.gora.pomodoro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddTaskActivity extends AppCompatActivity {

    private NumberPicker workTimePicker;
    private NumberPicker workBreakAmount;
    private Button buttonAddTask;
    private EditText editTaskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workTimePicker = findViewById(R.id.pickerWorkTime);
        workBreakAmount = findViewById(R.id.pickerBreakAmount);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        editTaskName = findViewById(R.id.editTaskName);

        workTimePicker.setMinValue(5);
        workTimePicker.setMaxValue(60);
        workTimePicker.setValue(25);
        workTimePicker.setWrapSelectorWheel(true);

        workBreakAmount.setMinValue(1);
        workBreakAmount.setMaxValue(10);
        workBreakAmount.setValue(3);
        workBreakAmount.setWrapSelectorWheel(true);

        buttonAddTask.setOnClickListener(v -> {
            // Get the inputted data
            String taskName = editTaskName.getText().toString().trim();
            if (taskName.isEmpty()) {
                taskName = "Yeni Görev";
            }
            int workMins = workTimePicker.getValue();
            int breakAmount = workBreakAmount.getValue();

            // Add to the static lists in MainActivity
            MainActivity.taskNames.add(taskName + " (" + workMins + " Dk)");
            MainActivity.taskWorkTimes.add(workMins);
            MainActivity.taskBreakAmounts.add(breakAmount);

            // Save tasks to persistence
            MainActivity.saveTasks(this);

            // Close this activity and go back
            finish();
        });
    }
}