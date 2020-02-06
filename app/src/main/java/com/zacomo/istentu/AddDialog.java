package com.zacomo.istentu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import java.text.DateFormat;
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

        dialogTitle = getString(R.string.addDialog_dialogTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        /*
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_add_btn_dialog, null);
        */

        View view = View.inflate(getContext(),R.layout.layout_add_btn_dialog, null);
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

        //assegno a bundle gli argomenti passati nella creazione di un nuovo addDialog
        //conterrà la lista di classi ed eventualmente i dati di un task (se addDialog è in modifica)
        bundle = getArguments();

        if (bundle != null){
            //se ho ricevuto una lista di classi nel bundle allora la devo inserire nello spinner
            if (bundle.containsKey("ClassList"))
                spinnerClasses = bundle.getStringArrayList("ClassList");

        }

        //Lavoro e casa sono le classi di default; se non sono già presenti le devo aggiungere
        if (spinnerClasses != null && !spinnerClasses.contains("Lavoro"))
            spinnerClasses.add(0,"Lavoro");
        if (spinnerClasses != null && !spinnerClasses.contains("Casa"))
            spinnerClasses.add(0,"Casa");

        // Inizializzazione adapter delle classi
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

        //se è presente un valore con chiave "taskName" significa che addDialog è stato chiamato per modifica
        if (bundle.containsKey("taskName")){
            editTask(spinnerClassesAdapter);
        }
        //Impostazione dell'aspetto del dialog
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
                        if (editTextInsertTaskName.getText().toString().trim().length() != 0) {
                            //azioni da intraprendere quando si chiude la finestra di dialogo "con successo"
                            String sClass ="";
                            if (spinnerInsertClass.getSelectedItem() != null)
                                sClass = spinnerInsertClass.getSelectedItem().toString();

                            Task newTask = new Task(editTextInsertTaskName.getText().toString().trim(),
                                    editTextInsertTaskDescription.getText().toString().trim(),
                                    spinnerInsertPriority.getSelectedItemPosition() + 1,
                                    taskDue, sClass);

                            //se presente la chiave "taskPosition" allora sono in modifica, pertanto devo
                            //mantenere la posizione del task pre modifica
                            if (bundle.containsKey("taskPosition"))
                                newTask.setTaskPosition(bundle.getInt("taskPosition"));

                            listener.insertData(newTask);
                        }
                        else{
                            String nameMissing = getString(R.string.addDialog_nameActivityMissing);
                            Toast.makeText(getContext(), nameMissing, Toast.LENGTH_SHORT).show();
                        }
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

        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(taskDue.getTime());

        textViewDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Cambio ora taskDue senza cambiare data
        taskDue.set(taskDue.get(Calendar.YEAR),taskDue.get(Calendar.MONTH),taskDue.get(Calendar.DAY_OF_MONTH),hourOfDay,minute,0);

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(taskDue.getTime());

        textViewTime.setText(time);
    }

    //metodo richiamato quando si  modifica un task
    //gestisce le apparenze del dialog di modifica in modo che mostri i parametri del task selezionato
    public void editTask(ArrayAdapter<String> spinnerClassAdapter){
        dialogTitle = "Modifica " + "\"" + bundle.getString("taskName") + "\"";
        editTextInsertTaskName.setText(bundle.getString("taskName"));

        //-1 perchè priorità va da 1 a 5 mentre gli indici vanno da 0 a 4
        spinnerInsertPriority.setSelection(bundle.getInt("taskPriority")-1);

        // con priority funziona perchè uso numeri spinnerInsertClass;
        spinnerClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsertClass.setAdapter(spinnerClassAdapter);

        if (bundle.getString("taskClass") != null){
            spinnerInsertClass.setSelection(spinnerClassAdapter.getPosition(bundle.getString("taskClass")));
        }

        editTextInsertTaskDescription.setText(bundle.getString("taskDescription"));
        textViewDate.setText(bundle.getString("taskDueDate"));
        textViewTime.setText(bundle.getString("taskDueTime"));
    }

}
