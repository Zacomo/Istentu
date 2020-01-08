package com.zacomo.istentu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SortDialog extends DialogFragment {

    private Spinner sortType;
    private Spinner sortOrder;

    private SortDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_sort_task_dialog, null);

        builder.setView(view)
                .setTitle("Ordina task")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer sType = sortType.getSelectedItemPosition();
                        Integer sOrder = sortOrder.getSelectedItemPosition();
                        listener.sortTasks(sType,sOrder);
                    }
                });

        sortType = view.findViewById(R.id.sortType);
        sortOrder = view.findViewById(R.id.sortOrder);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Occorre implementare SortDialogListener");
        }
    }

    public interface SortDialogListener{
        void sortTasks(Integer sType, Integer sOrder);
    }
}
