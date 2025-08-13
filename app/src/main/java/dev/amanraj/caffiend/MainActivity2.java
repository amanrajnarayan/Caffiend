//package dev.amanraj.caffiend;
//
//import static dev.amanraj.caffiend.MainActivity.dataList;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.AxisBase;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.ValueFormatter;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//
//public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
//
//    LineChart lineChart;
//    Button entriesButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//
//        entriesButton = findViewById(R.id.entriesButton);
//        entriesButton.setOnClickListener(this);
//
//        lineChart = findViewById(R.id.lineChart);
//        lineChart.setDragEnabled(true);
//        lineChart.setScaleEnabled(true);
//        lineChart.setMaxVisibleValueCount(10);
//
//        List<Entry> entries = new ArrayList<>();
//
//        for (String[] item : dataList) {
//            try {
//                float time = convertTimeToFloat(item[1]);
//                float amount = Float.parseFloat(item[0].replaceAll("[^\\d.]", ""));
//                entries.add(new Entry(time, amount));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        LineDataSet dataSet = new LineDataSet(entries, "Caffeine Intake Over Time");
//        dataSet.setColor(Color.MAGENTA);
//        dataSet.setCircleColor(Color.MAGENTA);
//        dataSet.setLineWidth(2f);
//        dataSet.setCircleRadius(4f);
//        dataSet.setValueTextColor(Color.DKGRAY);
//        dataSet.setValueTextSize(10f);
//        dataSet.setFillAlpha(110);
//
//        LineData lineData = new LineData(dataSet);
//        lineChart.setData(lineData);
//        lineChart.invalidate();
//
//        setupAxes();
//
//        // Text summaries
//        TextView currentAmountText = findViewById(R.id.currentAmountText);
//        TextView bedtimeText = findViewById(R.id.bedtimeText);
//        TextView intendedLimitText = findViewById(R.id.intendedLimitText);
//
//        float totalAmount = 0f;
//        for (String[] item : dataList) {
//            try {
//                totalAmount += Float.parseFloat(item[0].replaceAll("[^\\d.]", ""));
//            } catch (Exception ignored) {}
//        }
//
//        currentAmountText.setText("Current Amount: " + totalAmount + " mg");
//
//        bedtimeText.setText("Bedtime: 10:00 PM");
//        intendedLimitText.setText("Intended Limit: 400 mg");
//    }
//
//    private void setupAxes() {
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextSize(10f);
//        xAxis.setTextColor(Color.RED);
//        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLines(true);
//        xAxis.setGranularity(1f);
//        xAxis.setLabelRotationAngle(-45f);
//
//        YAxis yAxis = lineChart.getAxisLeft();
//        yAxis.setTextSize(10f);
//        yAxis.setTextColor(Color.RED);
//        yAxis.setDrawAxisLine(true);
//        yAxis.setDrawGridLines(true);
//        yAxis.setGranularity(1f);
//
//        lineChart.getAxisRight().setEnabled(false);
//
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                int hour = (int) value;
//                int minute = Math.round((value - hour) * 60);
//                return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
//            }
//        });
//
//        yAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                return String.format(Locale.getDefault(), "%.0f mg", value);
//            }
//        });
//    }
//
//    private float convertTimeToFloat(String timeStr) {
//        timeStr = timeStr.trim().toLowerCase(Locale.ROOT);
//        int hour = 0, minute = 0;
//
//        try {
//            if (timeStr.contains("am") || timeStr.contains("pm")) {
//                // e.g., 2:30 PM
//                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(sdf.parse(timeStr.toUpperCase()));
//                hour = cal.get(Calendar.HOUR_OF_DAY);
//                minute = cal.get(Calendar.MINUTE);
//            } else if (timeStr.contains(":")) {
//                // e.g., 14:30
//                String[] parts = timeStr.split(":");
//                hour = Integer.parseInt(parts[0]);
//                minute = Integer.parseInt(parts[1]);
//            }
//        } catch (ParseException | NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        return hour + (minute / 60f);
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.entriesButton) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
//    }
//}
package dev.amanraj.caffiend;

import static dev.amanraj.caffiend.MainActivity.dataList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    TextView currentAmountText, bedtimeText, intendedLimitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        entriesButton = findViewById(R.id.entriesButton);
        entriesButton.setOnClickListener(this);

        lineChart = findViewById(R.id.lineChart);
        currentAmountText = findViewById(R.id.currentAmountText);
        bedtimeText = findViewById(R.id.bedtimeText);
        intendedLimitText = findViewById(R.id.intendedLimitText);

        setupChart();
        updateTextValues();
    }

    private void setupChart() {
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setMaxVisibleValueCount(10);

        List<Entry> entries = new ArrayList<>();

        for (String[] i : dataList) {
            try {
                float time = Float.parseFloat(i[1].replace(":", "."));
                float amount = Float.parseFloat(i[0].replaceAll("[^\\d.]", ""));
                entries.add(new Entry(time, amount));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
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

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.CYAN);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(12f);
        yAxis.setTextColor(Color.CYAN);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.format(Locale.getDefault(), "%.1f", value).replace('.', ':');
            }
        });

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.format(Locale.getDefault(), "%.0f mg", value);
            }
        });
    }

    private void updateTextValues() {
        float currentAmount = 0;
        for (String[] i : dataList) {
            try {
                currentAmount += Float.parseFloat(i[0].replaceAll("[^\\d.]", ""));
            } catch (Exception ignored) {}
        }
        currentAmountText.setText("Current Amount: " + currentAmount + " mg");

        bedtimeText.setText("Bedtime: 10:00 PM");  // Replace with dynamic setting if needed
        intendedLimitText.setText("Intended Limit: 400 mg");
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
