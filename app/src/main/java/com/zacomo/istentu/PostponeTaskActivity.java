package com.zacomo.istentu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class PostponeTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private TextView date,time;
    private Calendar newDate;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_postpone_task);

        newDate = Calendar.getInstance();

        //se non c'è un intExtra allora position varrà -1
        position = getIntent().getIntExtra("position",-1);

        date = findViewById(R.id.textViewPostponeDate);
        time = findViewById(R.id.textViewPostponeTime);

        Button doneButton = findViewById(R.id.donePostponeButton);

        FloatingActionButton fabDatePicker = findViewById(R.id.fabPostponeDatePicker);
        FloatingActionButton fabTimePicker = findViewById(R.id.fabPostponeTimePicker);

        fabDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        fabTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButtonPressed();
                //chiude l'activity e richiama onDestroy()
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void doneButtonPressed(){
        //E' un problema il fatto che passo il context di questa activity e non della main?
        TaskFileHelper taskFileHelper = new TaskFileHelper(this);
        ArrayList<Task> tasks = taskFileHelper.readData("TaskList");
        
        if (position > -1 && position < tasks.size()){
            tasks.get(position).setTaskDue(newDate);
            taskFileHelper.writeData(tasks,"TaskList");
            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Not done", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        newDate.set(year,month,dayOfMonth,newDate.get(Calendar.HOUR_OF_DAY),newDate.get(Calendar.MINUTE));

        ++month;
        String d,m;
        if (dayOfMonth < 10)
            d = "0"+dayOfMonth;
        else
            d = Integer.toString(dayOfMonth);
        if (month < 10)
            m = "0"+month;
        else
            m = Integer.toString(month);
        String data = d + "/" + m + "/" + year;
        date.setText(data);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        newDate.set(newDate.get(Calendar.YEAR),newDate.get(Calendar.MONTH),newDate.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
        String h,m;
        if (hourOfDay < 10)
            h = "0"+hourOfDay;
        else
            h = Integer.toString(hourOfDay);
        if (minute < 10)
            m = "0"+minute;
        else
            m = Integer.toString(minute);

        time.setText(h + ":" + m);
    }
}
