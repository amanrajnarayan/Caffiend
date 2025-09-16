package dev.amanraj.caffiend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText limitInput;
    private Button bedtimeButton, saveButton, cancelButton;
    private int hour = 22, minute = 0; // default 10:00 PM

    private static final String PREF_NAME = "CaffeinePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        limitInput = findViewById(R.id.limitInput);
        bedtimeButton = findViewById(R.id.bedtimeButton);
        saveButton = findViewById(R.id.saveSettingsButton);
        cancelButton = findViewById(R.id.cancelSettingsButton);

        bedtimeButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedLimit = prefs.getString("UserLimit", "400");
        String savedBedtime = prefs.getString("UserBedtime", "10:00 PM");

        limitInput.setText(savedLimit);
        bedtimeButton.setText("Bedtime: " + savedBedtime);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bedtimeButton) {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
                hour = selectedHour;
                minute = selectedMinute;
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                bedtimeButton.setText("Bedtime: " + formattedTime);
            };

            int style = AlertDialog.THEME_HOLO_DARK;
            new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true).show();

        } else if (view.getId() == R.id.saveSettingsButton) {
            String newLimit = limitInput.getText().toString().trim();
            String bedtimeText = bedtimeButton.getText().toString().replace("Bedtime: ", "");

            if (newLimit.isEmpty()) {
                Toast.makeText(this, "Please enter a caffeine limit", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserLimit", newLimit);
            editor.putString("UserBedtime", bedtimeText);
            editor.apply();

            // Schedule bedtime reminder daily
            PeriodicWorkRequest bedtimeReminder =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class, 24, TimeUnit.HOURS)
                            .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "bedtime_reminder",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    bedtimeReminder
            );

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            finish(); // return to previous screen

        } else if (view.getId() == R.id.cancelSettingsButton) {
            finish(); // just close without saving
        }
    }
}
