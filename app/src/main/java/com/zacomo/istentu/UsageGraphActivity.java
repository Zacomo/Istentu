package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class UsageGraphActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    ArrayList<Task> mTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_graph);

        //forse devo passare context di main activity
        TaskFileHelper taskFileHelper = new TaskFileHelper(this);
        mTasks = taskFileHelper.readData("TaskList");

        anyChartView = findViewById(R.id.anyChartView);

        if (mTasks.size() > 0)
            setUpPieChart();
    }

    private void setUpPieChart(){
        Pie pie = AnyChart.pie();
        int[] numberTasksByPriority = new int[5];
        Arrays.fill(numberTasksByPriority, 0);

        ArrayList<DataEntry> dataEntries = new ArrayList<>();

        //con questo for conto quanti task di una determinata priorità ci sono
        for (int i = 0; i < mTasks.size(); i++)
            numberTasksByPriority[mTasks.get(i).getTaskPriority()-1]++;

        for (int i = 0; i < 5; i++)
            dataEntries.add(new ValueDataEntry(i,numberTasksByPriority[i]));

        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }
}
