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

    // Arayüz bileşenleri için değişkenler
    private Switch switchSound;
    private Switch switchVibration;
    private EditText editBreakTimeSettings;
    private Button buttonBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Ekran kenar boşluklarını ayarlayan varsayılan kod
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. XML'deki bileşenleri Java'ya bağlama
        switchSound = findViewById(R.id.switchSound);
        switchVibration = findViewById(R.id.switchVibration);
        editBreakTimeSettings = findViewById(R.id.editBreakTimeSettings);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);

        // 2. Uygulama açıldığında daha önce kaydedilmiş ayarları ekrana yükle
        loadSettings();

        // 3. Ana Ekrana Dönüş Butonuna Tıklanma Olayı
        if (buttonBackToMain != null) {
            buttonBackToMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Kullanıcı ekrandan çıkarken yaptığı değişiklikleri kaydet
                    saveSettings();

                    // Intent ile Ana Ekrana (MainActivity) geçiş yap
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Ayarlar sayfasını arka planda kapat (RAM'i rahatlat)
                }
            });
        }
    }

    // --- AYARLARI KAYDETME VE YÜKLEME METOTLARI ---

    // Kullanıcının yaptığı ayarları telefonun hafızasına kaydeder
    private void saveSettings() {
        // "PomodoroPrefs" adında gizli bir dosya oluşturuyoruz/açıyoruz
        SharedPreferences sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Switch'lerin Açık(true) veya Kapalı(false) durumlarını kaydediyoruz
        editor.putBoolean("soundEnabled", switchSound.isChecked());
        editor.putBoolean("vibrationEnabled", switchVibration.isChecked());

        // Mola süresini alıyoruz (Eğer kullanıcı kutuyu boş bırakırsa çökmemesi için kontrol yapıyoruz)
        String breakTimeStr = editBreakTimeSettings.getText().toString();
        int breakTime = 5; // Varsayılan değer
        if (!breakTimeStr.isEmpty()) {
            breakTime = Integer.parseInt(breakTimeStr);
        }
        editor.putInt("breakTime", breakTime);

        // Değişiklikleri uygula ve kaydet
        editor.apply();
    }

    // Telefonun hafızasındaki ayarları okuyup ekrandaki düğmelere aktarır
    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);

        // Verileri hafızadan çekiyoruz (Eğer daha önce hiç kaydedilmemişse varsayılan olarak true ve 5 değerini veriyoruz)
        boolean isSoundEnabled = sharedPreferences.getBoolean("soundEnabled", true);
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibrationEnabled", true);
        int breakTime = sharedPreferences.getInt("breakTime", 5);

        // Çektiğimiz verileri ekrandaki Switch'lere ve TextBox'a yerleştiriyoruz
        if (switchSound != null) switchSound.setChecked(isSoundEnabled);
        if (switchVibration != null) switchVibration.setChecked(isVibrationEnabled);
        if (editBreakTimeSettings != null) editBreakTimeSettings.setText(String.valueOf(breakTime));
    }
}