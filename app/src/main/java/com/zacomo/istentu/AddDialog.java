package com.zacomo.istentu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class AddDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText editTextInsertTaskName;
    private Spinner spinnerInsertPriority, spinnerInsertClass;
    private EditText editTextInsertTaskDescription;
    private TextView textViewDate, textViewTime;

    private Calendar taskDue;

    private AddDialogListener listener;

    private Bundle bundle;

    private String dialogTitle;

    private ArrayList<String> spinnerClasses;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {

        dialogTitle = "Nuovo Task";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_btn_dialog, null);

        FloatingActionButton fabDatePicker = view.findViewById(R.id.fabDatePicker);
        FloatingActionButton fabTimePicker = view.findViewById(R.id.fabTimePicker);

        editTextInsertTaskName = view.findViewById(R.id.insertTaskName);
        spinnerInsertPriority = view.findViewById(R.id.insertTaskPriority);
        spinnerInsertClass = view.findViewById(R.id.insertTaskClass);
        editTextInsertTaskDescription = view.findViewById(R.id.insertTaskDescription);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewTime = view.findViewById(R.id.textViewTime);

        taskDue = Calendar.getInstance();

        spinnerClasses = new ArrayList<>();
        spinnerClasses = getArguments().getStringArrayList("ClassList");

        if (!spinnerClasses.contains("Lavoro"))
            spinnerClasses.add(0,"Lavoro");

        if (!spinnerClasses.contains("Casa"))
            spinnerClasses.add(0,"Casa");

        Toast.makeText(getContext(), spinnerClasses.toString(), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> spinnerClassesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerClasses);
        spinnerClassesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInsertClass.setAdapter(spinnerClassesAdapter);


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

        if (bundle != null){
            editTask(bundle, spinnerClassesAdapter);
        }

        builder.setView(view)
                .setTitle(dialogTitle) //titolo finestra di dialogo
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //azioni da intraprendere quando si chiude la finestra di dialogo "senza successo"
                    }
                })
                .setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //azioni da intraprendere quando si chiude la finestra di dialogo "con successo"
                        String sClass ="";
                        if (spinnerInsertClass.getSelectedItem() != null)
                            sClass = spinnerInsertClass.getSelectedItem().toString();

                        Task newTask = new Task(editTextInsertTaskName.getText().toString(),
                                editTextInsertTaskDescription.getText().toString(),
                                spinnerInsertPriority.getSelectedItemPosition() + 1,
                                taskDue, sClass);

                        if (bundle != null)
                            newTask.setTaskPosition(bundle.getInt("taskPosition"));

                        listener.insertData(newTask);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "bisogna implementare AddDialogListener");
        }
    }

    public interface AddDialogListener{
        void insertData(Task newTask);
    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        //Cambio data taskDue senza cambiare ora
        taskDue.set(year,month,dayOfMonth,taskDue.get(Calendar.HOUR_OF_DAY),taskDue.get(Calendar.MINUTE),0);

        //month incrementato di uno perchè il conteggio inizia da 0 (Gen == 0)
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
        textViewDate.setText(data);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Cambio ora taskDue senza cambiare data
        taskDue.set(taskDue.get(Calendar.YEAR),taskDue.get(Calendar.MONTH),taskDue.get(Calendar.DAY_OF_MONTH),hourOfDay,minute,0);
        String h,m;
        if (hourOfDay < 10)
            h = "0"+hourOfDay;
        else
            h = Integer.toString(hourOfDay);
        if (minute < 10)
            m = "0"+minute;
        else
            m = Integer.toString(minute);

        textViewTime.setText(h + ":" + m);
    }

    //metodo da richiamare quando si vuole modificare un task
    public void editTask(Bundle savedInstanceState, ArrayAdapter<String> spinnerClassAdapter){
        dialogTitle = "Modifica " + "\"" + savedInstanceState.getString("taskName") + "\"";
        editTextInsertTaskName.setText(savedInstanceState.getString("taskName"));

        //-1 perchè priorità va da 1 a 5
        spinnerInsertPriority.setSelection(savedInstanceState.getInt("taskPriority")-1);

        // con priority funziona perchè uso numeri spinnerInsertClass;
        spinnerClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsertClass.setAdapter(spinnerClassAdapter);

        if (savedInstanceState.getString("taskClass") != null){
            spinnerInsertClass.setSelection(spinnerClassAdapter.getPosition(savedInstanceState.getString("taskClass")));
        }

        editTextInsertTaskDescription.setText(savedInstanceState.getString("taskDescription"));
        textViewDate.setText(savedInstanceState.getString("taskDueDate"));
        textViewTime.setText(savedInstanceState.getString("taskDueTime"));
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    public Bundle getBundle(){
        return this.bundle;
    }

}
