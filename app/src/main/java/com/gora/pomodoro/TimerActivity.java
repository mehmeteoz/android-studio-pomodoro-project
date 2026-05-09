package com.gora.pomodoro;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TimerActivity extends AppCompatActivity {

    // Arayüz bileşenleri için değişkenler
    private TextView textTimeLeft;
    private Button buttonStartPause;
    private ProgressBar progressBar;

    // Sayaç için gerekli arka plan değişkenleri
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long timeLeftInMillis;
    private long totalTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);

        // Ekran kenar boşluklarını ayarlayan mevcut kodunuz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. XML'deki bileşenleri Java'ya bağlama
        // DİKKAT: R.id. kısmındaki isimlerin activity_timer.xml dosyanızdaki id'ler ile birebir aynı olması gerekir.
        textTimeLeft = findViewById(R.id.textTimeLeft);
        buttonStartPause = findViewById(R.id.buttonStartPause);
        progressBar = findViewById(R.id.progressBar);

        // 2. Başlangıç süresini ayarlama (Örnek: 25 Dakika = 25 * 60 * 1000 milisaniye)
        totalTimeInMillis = 25 * 60 * 1000;
        timeLeftInMillis = totalTimeInMillis;

        // 3. ProgressBar'ın maksimum sınırını ayarlama
        if (progressBar != null) {
            progressBar.setMax((int) (totalTimeInMillis / 1000));
            progressBar.setProgress((int) (timeLeftInMillis / 1000));
        }

        // 4. Uygulama açılır açılmaz ekrana 25:00 yazdırmak için metodu çağırıyoruz
        updateCountDownText();

        // 5. Başlat/Durdur Butonuna tıklanma olayını dinleme
        if (buttonStartPause != null) {
            buttonStartPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timerRunning) {
                        pauseTimer(); // Sayaç çalışıyorsa durdur
                    } else {
                        startTimer(); // Sayaç duruyorsa başlat
                    }
                }
            });
        }
    }

    // --- SAYAÇ METOTLARI AŞAĞIDADIR ---

    // Sayacı Başlatma Metodu
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Her 1 saniyede (1000 milisaniye) bir bu kısım çalışır
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();

                // ProgressBar'ı kalan süreye göre günceller
                if (progressBar != null) {
                    progressBar.setProgress((int) (timeLeftInMillis / 1000));
                }
            }

            @Override
            public void onFinish() {
                // Süre tamamen bittiğinde bu kısım çalışır
                timerRunning = false;
                if (buttonStartPause != null) {
                    buttonStartPause.setText("Süre Bitti!");
                }
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
            }
        }.start();

        timerRunning = true;
        if (buttonStartPause != null) {
            buttonStartPause.setText("Durdur");
        }
    }

    // Sayacı Duraklatma Metodu
    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        if (buttonStartPause != null) {
            buttonStartPause.setText("Devam Et");
        }
    }

    // Kalan süreyi Dakika:Saniye formatına (Örn: 24:59) çeviren metot
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // %02d formatı, sayının tek haneli olması durumunda başına 0 ekler
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);

        if (textTimeLeft != null) {
            textTimeLeft.setText(timeLeftFormatted);
        }
    }
}