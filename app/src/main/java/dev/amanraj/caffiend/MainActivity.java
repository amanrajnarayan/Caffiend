package dev.amanraj.caffiend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText amountInput;
    Button timeInput, entryButton, sideButton, fyiButton;
    int hour, minute;

    static List<String[]> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    CaffeineAdapter adapter;

    private static final String PREF_NAME = "CaffeinePrefs";
    private static final String DATA_KEY = "CaffeineData";
    private static final String DATE_KEY = "SavedDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amountInput);
        amountInput.setHint("ex: 23 mg");

        timeInput = findViewById(R.id.timeInput);
        timeInput.setHint("ex: 2:00 PM");

        entryButton = findViewById(R.id.entryButton);
        sideButton = findViewById(R.id.sideButton);
        fyiButton = findViewById(R.id.FYI);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CaffeineAdapter(dataList, this);
        recyclerView.setAdapter(adapter);

        entryButton.setOnClickListener(this);
        sideButton.setOnClickListener(this);
        timeInput.setOnClickListener(this);

        loadSavedData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.entryButton) {
            if (amountInput.getText().toString().isEmpty() || timeInput.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }

            String amount = amountInput.getText().toString();
            String time = timeInput.getText().toString();

            dataList.add(new String[]{amount, time});
            sort(dataList);
            adapter.notifyDataSetChanged();
            saveData();

            amountInput.setText("");
            timeInput.setText("");
        } else if (view.getId() == R.id.sideButton) {
            Intent i = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(i);
        } else if (view.getId() == R.id.timeInput) {
            TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
                hour = selectedHour;
                minute = selectedMinute;
                timeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            };

            int style = AlertDialog.THEME_HOLO_DARK;
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        } else if (view.getId()==R.id.FYI) {
            Intent fyi_intent = new Intent(getApplicationContext(), MainActivity3.class);
            startActivity((fyi_intent));
        }
    }

    public void sort(List<String[]> list) {
        list.sort((a, b) -> {
            try {
                String[] t1 = a[1].split(":");
                String[] t2 = b[1].split(":");
                int hour1 = Integer.parseInt(t1[0]);
                int min1 = Integer.parseInt(t1[1]);
                int hour2 = Integer.parseInt(t2[0]);
                int min2 = Integer.parseInt(t2[1]);

                int timeA = hour1 * 60 + min1;
                int timeB = hour2 * 60 + min2;

                return Integer.compare(timeA, timeB);
            } catch (Exception e) {
                return 0;
            }
        });
    }

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

        sort(dataList);
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


}
