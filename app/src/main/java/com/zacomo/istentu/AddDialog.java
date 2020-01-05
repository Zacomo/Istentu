package com.zacomo.istentu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class AddDialog extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText editTextInsertTaskName;
    private Spinner spinnerInsertPriority, spinnerInsertClass;
    private EditText editTextInsertTaskDescription;
    private TextView textViewDate;

    private Calendar taskDue;

    private AddDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_btn_dialog, null);

        builder.setView(view)
                .setTitle("Nuova attività") //titolo finestra di dialogo
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
                        listener.insertData(
                                editTextInsertTaskName.getText().toString(),
                                editTextInsertTaskDescription.getText().toString(),
                                spinnerInsertPriority.getSelectedItemPosition() + 1,
                                taskDue, spinnerInsertClass.getSelectedItem().toString());
                    }
                });

        FloatingActionButton fabDatePicker = view.findViewById(R.id.fabDatePicker);

        editTextInsertTaskName = view.findViewById(R.id.insertTaskName);
        spinnerInsertPriority = view.findViewById(R.id.insertTaskPriority);
        spinnerInsertClass = view.findViewById(R.id.insertTaskClass);
        editTextInsertTaskDescription = view.findViewById(R.id.insertTaskDescription);
        textViewDate = view.findViewById(R.id.textViewDate);

        taskDue = Calendar.getInstance();

        ArrayList<String> spinnerClasses = new ArrayList<>();
        spinnerClasses.add("Casa");
        spinnerClasses.add("Lavoro");
        spinnerClasses.add("Studio");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerInsertClass.setAdapter(adapter);

        fabDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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
        void insertData(String taskName, String taskDescription, int taskPriority, Calendar taskDue, String taskClass);
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        taskDue.set(year,month,dayOfMonth);

        //month incrementato di uno perchè il conteggio inizia da 0 (Gen == 0)
        String data = year + "/" + ++month + "/" + dayOfMonth;
        textViewDate.setText(data);
    }

}
