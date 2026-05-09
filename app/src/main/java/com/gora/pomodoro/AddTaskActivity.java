package com.gora.pomodoro;

import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddTaskActivity extends AppCompatActivity {

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

        NumberPicker workTimePicker = findViewById(R.id.pickerWorkTime);
        NumberPicker workBreakAmount = findViewById(R.id.pickerBreakAmount);

        workTimePicker.setMinValue(5);
        workTimePicker.setMaxValue(60);
        workTimePicker.setValue(25);
        workTimePicker.setWrapSelectorWheel(true);

        workBreakAmount.setMaxValue(10);
        workBreakAmount.setMinValue(1);
        workBreakAmount.setValue(3);
        workBreakAmount.setWrapSelectorWheel(true);




    }
}