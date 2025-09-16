package dev.amanraj.caffiend;

import static dev.amanraj.caffiend.MainActivity.dataList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    LineChart lineChart;
    Button entriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        entriesButton = findViewById(R.id.entriesButton);
        entriesButton.setOnClickListener(this);

        lineChart = findViewById(R.id.lineChart);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setMaxVisibleValueCount(10);

        List<Entry> entries = new ArrayList<>();

        // ✅ Build entries for chart
        for (String[] i : dataList) {
            try {
                // Parse time in HH:mm → minutes of day
                String[] parts = i[1].split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                float timeInMinutes = hour * 60 + minute;

                float amount = Float.parseFloat(i[0].replaceAll("[^0-9]", ""));
                entries.add(new Entry(timeInMinutes, amount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Caffeine Intake Over Time");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setCircleColor(Color.MAGENTA);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setFillAlpha(110);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // ✅ X Axis (Time in hours)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(60f); // 1 hr intervals

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int hours = (int) (value / 60);
                int minutes = (int) (value % 60);
                return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            }
        });

        // ✅ Y Axis (mg values)
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(10f);
        yAxis.setTextColor(Color.RED);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(true);
        yAxis.setGranularity(50f);
        lineChart.getAxisRight().setEnabled(false);

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.format(Locale.getDefault(), "%.0f mg", value);
            }
        });

        // ✅ Stats below chart
        TextView currentAmountText = findViewById(R.id.currentAmountText);
        TextView bedtimeText = findViewById(R.id.bedtimeText);
        TextView intendedLimitText = findViewById(R.id.intendedLimitText);

        float currentAmount = 0;
        for (String[] i : dataList) {
            try {
                currentAmount += Float.parseFloat(i[0].replaceAll("[^0-9]", ""));
            } catch (Exception ignored) {}
        }

        SharedPreferences prefs = getSharedPreferences("CaffeinePrefs", MODE_PRIVATE);
        int limit = Integer.parseInt(prefs.getString("UserLimit", "400"));
        String bedtime = prefs.getString("UserBedtime", "10:00 PM");

        currentAmountText.setText("Current Amount: " + currentAmount + " mg");
        bedtimeText.setText("Bedtime: " + bedtime);
        intendedLimitText.setText("Intended Limit: " + limit + " mg");

        // ✅ Color warning
        if (currentAmount > limit) {
            currentAmountText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else if (currentAmount > (0.8 * limit)) {
            currentAmountText.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            currentAmountText.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.entriesButton) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
