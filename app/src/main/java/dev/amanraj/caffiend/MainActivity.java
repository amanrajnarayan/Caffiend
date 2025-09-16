package dev.amanraj.caffiend;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText amountInput;
    Button timeInput, entryButton, sideButton, fyiButton, settingsButton;
    RecyclerView recyclerView;
    CaffeineAdapter adapter;

    public static List<String[]> dataList = new ArrayList<>();

    private static final String PREF_NAME = "CaffeinePrefs";
    private static final String DATA_KEY = "CaffeineData";
    private static final String DATE_KEY = "SavedDate";

    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private int hour = 0, minute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amountInput);
        timeInput = findViewById(R.id.timeInput);
        entryButton = findViewById(R.id.entryButton);
        sideButton = findViewById(R.id.sideButton);
        fyiButton = findViewById(R.id.FYI);
        settingsButton = findViewById(R.id.settingsButton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CaffeineAdapter(dataList, this, this::saveData);
        recyclerView.setAdapter(adapter);

        entryButton.setOnClickListener(this);
        sideButton.setOnClickListener(this);
        fyiButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        timeInput.setOnClickListener(this);

        checkAndRequestNotificationPermission();
        loadSavedData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.entryButton) {
            String amount = amountInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();

            if (amount.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please enter amount and select time", Toast.LENGTH_SHORT).show();
                return;
            }

            dataList.add(new String[]{amount, time});
            adapter.notifyDataSetChanged();
            saveData();
            checkCaffeineLimit();

            amountInput.setText("");
            timeInput.setText("");

        } else if (v.getId() == R.id.sideButton) {
            startActivity(new Intent(this, MainActivity2.class));
        } else if (v.getId() == R.id.FYI) {
            startActivity(new Intent(this, MainActivity3.class));
        } else if (v.getId() == R.id.settingsButton) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (v.getId() == R.id.timeInput) {
            TimePickerDialog.OnTimeSetListener onTimeSetListener =
                    (timePicker, selectedHour, selectedMinute) -> {
                        hour = selectedHour;
                        minute = selectedMinute;
                        String formatted = String.format(Locale.getDefault(),
                                "%02d:%02d", hour, minute);
                        timeInput.setText(formatted);
                    };

            int style = AlertDialog.THEME_HOLO_DARK;
            new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true).show();
        }
    }

    // ✅ Check caffeine limit and send notification if exceeded
    private void checkCaffeineLimit() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int limit = Integer.parseInt(prefs.getString("UserLimit", "400"));

        int total = 0;
        for (String[] entry : dataList) {
            try {
                total += Integer.parseInt(entry[0].replaceAll("[^0-9]", ""));
            } catch (Exception ignored) {}
        }

        if (total > limit) {
            sendOverLimitNotification(total, limit);
        }
    }

    private void sendOverLimitNotification(int total, int limit) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "caffeine_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Caffeine Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠️ Caffeine Limit Exceeded")
                .setContentText("You've consumed " + total + " mg (Limit: " + limit + " mg)")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // ✅ Auto-reset at midnight
    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedDate = prefs.getString(DATE_KEY, "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!today.equals(savedDate)) {
            prefs.edit().clear().apply(); // clear all data at midnight
            dataList.clear();
            return;
        }

        String rawData = prefs.getString(DATA_KEY, "");
        dataList.clear();
        if (!rawData.isEmpty()) {
            String[] entries = rawData.split(";");
            for (String entry : entries) {
                String[] values = entry.split(",");
                if (values.length == 2) {
                    dataList.add(values);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder builder = new StringBuilder();
        for (String[] entry : dataList) {
            builder.append(entry[0]).append(",").append(entry[1]).append(";");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        editor.putString(DATA_KEY, builder.toString());
        editor.putString(DATE_KEY, today);
        editor.apply();
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }
}
