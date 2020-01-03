package com.zacomo.istentu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;

public class AddDialog extends AppCompatDialogFragment{

    private EditText editTextInsertTaskName;
    private Spinner spinnerInsertPriority;
    private EditText editTextInsertTaskDescription;

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
                                null);
                    }
                });

        editTextInsertTaskName = view.findViewById(R.id.insertTaskName);
        spinnerInsertPriority = view.findViewById(R.id.insertTaskPriority);
        editTextInsertTaskDescription = view.findViewById(R.id.insertTaskDescription);

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
        void insertData(String taskName, String taskDescription, int taskPriority, Calendar taskDue);
    }

}
