package com.gora.pomodoro;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAddTask;
    private Button buttonSettings;
    private ListView listViewTasks;

    public static ArrayList<String> taskNames = new ArrayList<>();
    public static ArrayList<Integer> taskWorkTimes = new ArrayList<>();
    public static ArrayList<Integer> taskBreakAmounts = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabAddTask = findViewById(R.id.fabAddTask);
        buttonSettings = findViewById(R.id.buttonSettings);
        listViewTasks = findViewById(R.id.listViewTasks);

        if (taskNames.isEmpty()) {
            taskNames.add("Örnek Görev (25 Dk)");
            taskWorkTimes.add(25);
            taskBreakAmounts.add(3);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskNames);
        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            if (TimerService.isServiceRunning) {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Zaten çalışan bir sayaç var!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                intent.putExtra("WORK_TIME", taskWorkTimes.get(position));
                intent.putExtra("TASK_NAME", taskNames.get(position));
                intent.putExtra("BREAK_AMOUNT", taskBreakAmounts.get(position));
                startActivity(intent);
            }
        });

        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Görevi Sil")
                    .setMessage("Bu görevi silmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        taskNames.remove(position);
                        taskWorkTimes.remove(position);
                        taskBreakAmounts.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
            return true;
        });

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
