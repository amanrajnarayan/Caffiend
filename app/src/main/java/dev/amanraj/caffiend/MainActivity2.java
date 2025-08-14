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
