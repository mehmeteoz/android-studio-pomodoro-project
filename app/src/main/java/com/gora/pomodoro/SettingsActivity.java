package com.gora.pomodoro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchSound;
    private Switch switchCustomSound;
    private Switch switchVibration;
    private EditText editBreakTimeSettings;
    private Button buttonBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchSound = findViewById(R.id.switchSound);
        switchCustomSound = findViewById(R.id.switchCustomSound);
        switchVibration = findViewById(R.id.switchVibration);
        editBreakTimeSettings = findViewById(R.id.editBreakTimeSettings);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);

        loadSettings();

        if (buttonBackToMain != null) {
            buttonBackToMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSettings();
                    finish();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }

    private void saveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (switchSound != null) editor.putBoolean("soundEnabled", switchSound.isChecked());
        if (switchCustomSound != null) editor.putBoolean("customSoundEnabled", switchCustomSound.isChecked());
        if (switchVibration != null) editor.putBoolean("vibrationEnabled", switchVibration.isChecked());

        if (editBreakTimeSettings != null) {
            String breakTimeStr = editBreakTimeSettings.getText().toString();
            int breakTime = 5;
            if (!breakTimeStr.isEmpty()) {
                try {
                    breakTime = Integer.parseInt(breakTimeStr);
                } catch (NumberFormatException e) {
                    breakTime = 5;
                }
            }
            editor.putInt("breakTime", breakTime);
        }

        editor.apply();
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);

        boolean isSoundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        boolean isCustomSoundEnabled = sharedPreferences.getBoolean("customSoundEnabled", false);
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", true);
        int breakTime = sharedPreferences.getInt("breakTime", 5);

        if (switchSound != null) switchSound.setChecked(isSoundEnabled);
        if (switchCustomSound != null) switchCustomSound.setChecked(isCustomSoundEnabled);
        if (switchVibration != null) switchVibration.setChecked(isVibrationEnabled);
        if (editBreakTimeSettings != null) editBreakTimeSettings.setText(String.valueOf(breakTime));
    }
}
