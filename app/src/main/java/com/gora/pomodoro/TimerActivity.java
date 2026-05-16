package com.gora.pomodoro;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private TextView textTimeLeft, phaseIndicator, taskNameTitle, textPomodoroCount, textDescription;
    private ProgressBar progressBar;
    private Button buttonStartPause, buttonSkip, buttonBack, buttonFinishEarly;

    private TimerService timerService;
    private boolean isBound = false;

    // Receives real-time updates from the Service
    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("TimerUpdate".equals(intent.getAction())) {
                updateUIFromIntent(intent);
            } else if ("TimerFinished".equals(intent.getAction())) {
                Toast.makeText(TimerActivity.this, "Görev bitti!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            isBound = true;
            syncWithService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        textTimeLeft = findViewById(R.id.textTimeLeft);
        phaseIndicator = findViewById(R.id.taskName);
        taskNameTitle = findViewById(R.id.TaskName);
        textPomodoroCount = findViewById(R.id.textPomodoroCount);
        textDescription = findViewById(R.id.description);
        progressBar = findViewById(R.id.progressBar);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        buttonSkip = findViewById(R.id.pause2);
        buttonBack = findViewById(R.id.pause3);
        buttonFinishEarly = findViewById(R.id.finishEarly);

        Intent intent = getIntent();
        // Start the service only if we are passing new task data
        if (intent.hasExtra("WORK_TIME") && !TimerService.isServiceRunning) {
            startTimerService(intent);
        }

        buttonStartPause.setOnClickListener(v -> {
            if (isBound && timerService != null) {
                if (timerService.isTimerRunning()) {
                    timerService.pauseTimer();
                } else {
                    timerService.startTimer();
                }
            }
        });

        buttonSkip.setOnClickListener(v -> {
            if (isBound && timerService != null) {
                timerService.skipPhase();
            }
        });

        buttonFinishEarly.setOnClickListener(v -> {
            // Prevent accidental clicks with a confirmation dialog
            new AlertDialog.Builder(TimerActivity.this)
                    .setTitle("Görevi Erken Bitir")
                    .setMessage("Bu görevi henüz tamamlamadınız. Erken bitirmek istediğinize emin misiniz?")
                    .setPositiveButton("Evet, Bitir", (dialog, which) -> {
                        // This code only runs if the user clicks "Evet"
                        if (isBound && timerService != null) {
                            timerService.finishTaskEarly();
                        }
                    })
                    .setNegativeButton("İptal", null) // Clicking "İptal" just dismisses the dialog
                    .show();
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void startTimerService(Intent intent) {
        SharedPreferences prefs = getSharedPreferences("PomodoroPrefs", MODE_PRIVATE);
        int breakMins = prefs.getInt("breakTime", 5);

        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("WORK_TIME", intent.getIntExtra("WORK_TIME", 25));
        serviceIntent.putExtra("BREAK_AMOUNT", intent.getIntExtra("BREAK_AMOUNT", 2));
        serviceIntent.putExtra("TASK_NAME", intent.getStringExtra("TASK_NAME"));
        serviceIntent.putExtra("TASK_DESC", intent.getStringExtra("TASK_DESC"));
        serviceIntent.putExtra("BREAK_MINS", breakMins);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void syncWithService() {
        if (isBound && timerService != null) {
            String name = timerService.getTaskName();
            if (name != null) taskNameTitle.setText(name);

            String desc = timerService.getTaskDesc();
            if (desc != null) textDescription.setText(desc);
            
            updateUI(timerService.getTimeLeftInMillis(), 
                     timerService.getDisplayTime(),
                     timerService.isWorkPhase(), 
                     timerService.isTimerRunning(), 
                     timerService.getCurrentSegment(), 
                     timerService.getTotalSegments(), 
                     timerService.getMaxDuration());
        }
    }

    private void updateUIFromIntent(Intent intent) {
        long timeLeft = intent.getLongExtra("timeLeftInMillis", 0);
        long displayTime = intent.getLongExtra("displayTime", 0);
        boolean isWork = intent.getBooleanExtra("isWorkPhase", true);
        boolean running = intent.getBooleanExtra("timerRunning", false);
        int current = intent.getIntExtra("currentSegment", 1);
        int total = intent.getIntExtra("totalSegments", 1);
        String name = intent.getStringExtra("taskName");
        String desc = intent.getStringExtra("taskDesc");
        long maxDuration = intent.getLongExtra("maxDuration", 1000);

        if (name != null) taskNameTitle.setText(name);
        if (desc != null) textDescription.setText(desc);
        updateUI(timeLeft, displayTime, isWork, running, current, total, maxDuration);
    }

    private void updateUI(long timeLeft, long displayTime, boolean isWork, boolean running, int current, int total, long maxDuration) {
        // Display current segment time remaining
        int minutes = (int) (displayTime / 1000) / 60;
        int seconds = (int) (displayTime / 1000) % 60;
        textTimeLeft.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

        if (textPomodoroCount != null) {
            textPomodoroCount.setText(String.format(Locale.getDefault(), "%d / %d", current, total));
        }

        if (isWork) {
            phaseIndicator.setText("Çalışma");
        } else {
            phaseIndicator.setText("Mola");
        }

        buttonStartPause.setText(running ? "Durdur" : "Başlat");
        
        // Progress bar synced to current phase's local duration
        progressBar.setMax((int) (maxDuration / 1000));
        progressBar.setProgress((int) (timeLeft / 1000));
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to service
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        
        IntentFilter filter = new IntentFilter();
        filter.addAction("TimerUpdate");
        filter.addAction("TimerFinished");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timerReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(timerReceiver, filter);
        }
        
        // Manual sync if already bound (re-entering activity)
        if (isBound) {
            syncWithService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        try {
            unregisterReceiver(timerReceiver);
        } catch (IllegalArgumentException e) {
            // Already unregistered
        }
    }
}
