package com.zacomo.istentu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PostponeTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private ArrayList<Task> tasks;
    private TaskFileHelper taskFileHelper;
    private TextView textViewDate,textViewTime;
    private Calendar newDate;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpone_task);

        taskFileHelper = new TaskFileHelper(this);

        if (taskFileHelper.readData("TaskList").isEmpty())
            tasks = new ArrayList<>();
        else
            tasks = taskFileHelper.readData("TaskList");

        newDate = Calendar.getInstance();

        //se non c'è un intExtra allora position varrà -1
        position = getIntent().getIntExtra("position",-1);

        textViewDate = findViewById(R.id.textViewPostponeDate);
        textViewTime = findViewById(R.id.textViewPostponeTime);

        Toolbar toolbar = findViewById(R.id.postponeActivityHeader);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.postponeTaskActivity_header_title));
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (tasks.size() > 0 && position > -1){
            textViewDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(tasks.get(position).getTaskDue().getTime()));
            textViewTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(tasks.get(position).getTaskDue().getTime()));
        }

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
                finishAndRemoveTask();
            }
        });

    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                tasks.get(position).getTaskDue().get(Calendar.YEAR),
                tasks.get(position).getTaskDue().get(Calendar.MONTH),
                tasks.get(position).getTaskDue().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                tasks.get(position).getTaskDue().get(Calendar.HOUR_OF_DAY),
                tasks.get(position).getTaskDue().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void doneButtonPressed(){

        //se la posizione del task inserito è valida, allora aggiorno la data e salvo i cambiamenti
        if (position > -1 && position < tasks.size()){
            tasks.get(position).setTaskDue(newDate);
            taskFileHelper.writeData(tasks,"TaskList");
            String doneText = getString(R.string.done_text);
            Toast.makeText(this, doneText, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Not done, out of bounds", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        newDate.set(year,month,dayOfMonth,newDate.get(Calendar.HOUR_OF_DAY),newDate.get(Calendar.MINUTE),0);

        textViewDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(newDate.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        newDate.set(newDate.get(Calendar.YEAR),newDate.get(Calendar.MONTH),newDate.get(Calendar.DAY_OF_MONTH),hourOfDay,minute,0);
        textViewTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(newDate.getTime()));
    }
}
