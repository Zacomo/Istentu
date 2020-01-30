package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.Arrays;

public class UsageGraphActivity extends AppCompatActivity {

    AnyChartView anyChartViewPriority, anyChartViewStatus;
    ArrayList<Task> mTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_graph);

        //forse devo passare context di main activity
        TaskFileHelper taskFileHelper = new TaskFileHelper(this);
        mTasks = taskFileHelper.readData("TaskList");
        Spinner spinner = findViewById(R.id.spinnerChartType);
        anyChartViewPriority = findViewById(R.id.anyChartViewPriority);
        anyChartViewStatus = findViewById(R.id.anyChartViewStatus);

        if (mTasks.size() > 0) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            setUpPriorityPieChart();
                            break;
                        case 1:
                            setUpTaskStatusPieChart();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void setUpPriorityPieChart(){
        anyChartViewStatus.setVisibility(View.INVISIBLE);
        anyChartViewPriority.setVisibility(View.VISIBLE);

        APIlib.getInstance().setActiveAnyChartView(anyChartViewPriority);

        Pie pie = AnyChart.pie();
        int[] numberTasksByPriority = new int[5];
        Arrays.fill(numberTasksByPriority, 0);

        ArrayList<DataEntry> dataEntries = new ArrayList<>();

        //con questo for conto quanti task di una determinata priorità ci sono
        for (int i = 0; i < mTasks.size(); i++)
            numberTasksByPriority[mTasks.get(i).getTaskPriority()-1]++;

        for (int i = 0; i < 5; i++)
            dataEntries.add(new ValueDataEntry("P: "+ Integer.toString(i+1),numberTasksByPriority[i]));

        pie.labels(true);
        pie.title("Distribuzione dei task per priorità (P)");
        pie.legend().title("Priorità");
        pie.legend().align("top");
        pie.legend().maxWidth("30%");
        pie.legend().itemsLayout("verticalExpandable");
        pie.legend().position("left");
        pie.legend().paginator().orientation("bottom");
        pie.data(dataEntries);

        anyChartViewPriority.setChart(pie);
    }

    private void setUpTaskStatusPieChart(){
        anyChartViewPriority.setVisibility(View.INVISIBLE);
        anyChartViewStatus.setVisibility(View.VISIBLE);
        
        APIlib.getInstance().setActiveAnyChartView(anyChartViewStatus);

        Pie pie = AnyChart.pie();
        int[] numberTasksByStatus = new int[3];
        Arrays.fill(numberTasksByStatus, 0);

        ArrayList<DataEntry> dataEntries = new ArrayList<>();

        //con questo for conto quanti task di una determinata priorità ci sono
        for (int i = 0; i < mTasks.size(); i++){
            switch (mTasks.get(i).getTaskStatus()){
                case 0: //task in attesa
                    numberTasksByStatus[0]++;
                    break;
                case 1: //task in corso
                    numberTasksByStatus[1]++;
                    break;
                case 2: //task completato
                    numberTasksByStatus[2]++;
                    break;
                default:
                    break;
            }
        }

        dataEntries.add(new ValueDataEntry("Status: in attesa",numberTasksByStatus[0]));
        dataEntries.add(new ValueDataEntry("Status: in corso",numberTasksByStatus[1]));
        dataEntries.add(new ValueDataEntry("Status: completato",numberTasksByStatus[2]));

        pie.labels(true);
        pie.title("Distribuzione dei task per stato");
        pie.legend().title("Stato");
        pie.legend().align("top");
        pie.legend().maxWidth("40%");
        pie.legend().itemsLayout("verticalExpandable");
        pie.legend().position("left");
        pie.legend().paginator().orientation("bottom");
        pie.data(dataEntries);

        anyChartViewStatus.setChart(pie);

        Toast.makeText(this, "Helo", Toast.LENGTH_SHORT).show();
    }
}
