package com.gora.pomodoro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.Locale;

public class TimerService extends Service {

    public static boolean isServiceRunning = false;
    public static final String ACTION_PAUSE = "com.gora.pomodoro.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.gora.pomodoro.ACTION_RESUME";

    private final IBinder binder = new TimerBinder();
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private boolean timerRunning = false;
    private boolean isWorkPhase = true;
    private int currentSegment = 1;
    private int totalSegments;
    private long segmentDurationMillis;
    private long breakDurationMillis;
    private String taskName;
    private boolean isInitialized = false;

    private static final String CHANNEL_ID = "TimerServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    public class TimerBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_PAUSE)) {
                pauseTimer();
                return START_STICKY;
            } else if (action.equals(ACTION_RESUME)) {
                startTimer();
                return START_STICKY;
            }
        }

        if (intent != null && intent.hasExtra("WORK_TIME") && !isInitialized) {
            taskName = intent.getStringExtra("TASK_NAME");
            int workMins = intent.getIntExtra("WORK_TIME", 25);
            int breakAmount = intent.getIntExtra("BREAK_AMOUNT", 2);
            int breakMins = intent.getIntExtra("BREAK_MINS", 5);

            totalSegments = breakAmount + 1;
            segmentDurationMillis = workMins * 60000L;
            breakDurationMillis = breakMins * 60000L;
            
            timeLeftInMillis = segmentDurationMillis;
            isWorkPhase = true;
            currentSegment = 1;
            isInitialized = true;
            
            startForegroundServiceWithNotification(getFormattedStatusText());
            startTimer();
        } else {
            // Service already running or restarted, sync current state
            startForegroundServiceWithNotification(getFormattedStatusText());
            updateNotification();
            sendBroadcastUpdate();
        }
        return START_STICKY;
    }

    private void startForegroundServiceWithNotification(String text) {
        Notification notification = getNotification(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private String getFormattedStatusText() {
        return (isWorkPhase ? "Çalışma" : "Mola") + " - " + formatTime(getDisplayTime());
    }

    public void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
                sendBroadcastUpdate();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                goToNextPhase();
            }
        }.start();
        timerRunning = true;
        updateNotification();
        sendBroadcastUpdate();
    }

    public void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = false;
        updateNotification();
        sendBroadcastUpdate();
    }

    public void skipPhase() {
        pauseTimer();
        goToNextPhase();
    }

    public void finishTaskEarly() {
        pauseTimer();
        isServiceRunning = false;
        stopForeground(STOP_FOREGROUND_REMOVE);
        sendBroadcastFinished();
        stopSelf();
    }

    private void goToNextPhase() {
        if (isWorkPhase) {
            if (currentSegment >= totalSegments) {
                finishTaskEarly();
                return;
            }
            isWorkPhase = false;
            timeLeftInMillis = breakDurationMillis;
        } else {
            currentSegment++;
            isWorkPhase = true;
            timeLeftInMillis = segmentDurationMillis;
        }
        startTimer();
    }

    private void updateNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null && isServiceRunning) {
            manager.notify(NOTIFICATION_ID, getNotification(getFormattedStatusText()));
        }
    }

    public long getDisplayTime() {
        return timeLeftInMillis;
    }

    private Notification getNotification(String contentText) {
        Intent notificationIntent = new Intent(this, TimerActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(taskName != null ? taskName : "Pomodoro")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSilent(true) // Keeps it quiet during updates
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);

        if (timerRunning) {
            Intent pauseIntent = new Intent(this, TimerService.class);
            pauseIntent.setAction(ACTION_PAUSE);
            PendingIntent pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(android.R.drawable.ic_media_pause, "Duraklat", pausePendingIntent);
        } else {
            Intent resumeIntent = new Intent(this, TimerService.class);
            resumeIntent.setAction(ACTION_RESUME);
            PendingIntent resumePendingIntent = PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE);
            builder.addAction(android.R.drawable.ic_media_play, "Devam Et", resumePendingIntent);
        }

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setSound(null, null);
            serviceChannel.enableVibration(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private String formatTime(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void sendBroadcastUpdate() {
        Intent intent = new Intent("TimerUpdate");
        intent.setPackage(getPackageName());
        intent.putExtra("timeLeftInMillis", timeLeftInMillis);
        intent.putExtra("displayTime", getDisplayTime());
        intent.putExtra("isWorkPhase", isWorkPhase);
        intent.putExtra("timerRunning", timerRunning);
        intent.putExtra("currentSegment", currentSegment);
        intent.putExtra("totalSegments", totalSegments);
        intent.putExtra("taskName", taskName);
        intent.putExtra("maxDuration", isWorkPhase ? segmentDurationMillis : breakDurationMillis);
        sendBroadcast(intent);
    }

    private void sendBroadcastFinished() {
        isServiceRunning = false;
        Intent intent = new Intent("TimerFinished");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }

    public long getTimeLeftInMillis() { return timeLeftInMillis; }
    public boolean isTimerRunning() { return timerRunning; }
    public boolean isWorkPhase() { return isWorkPhase; }
    public int getCurrentSegment() { return currentSegment; }
    public int getTotalSegments() { return totalSegments; }
    public String getTaskName() { return taskName; }
    public long getMaxDuration() { return isWorkPhase ? segmentDurationMillis : breakDurationMillis; }
}
