package com.zacomo.istentu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.fragment.app.Fragment;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<Task> mTasks;
    private Context mContext;

    private FileHelper fileHelper;

    public RecyclerViewAdapter(Context context, ArrayList<Task> tasks, FileHelper fileHelper) {
        this.mTasks = tasks;
        this.mContext = context;
        this.fileHelper = fileHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.taskName.setText(mTasks.get(position).getTaskName());
        holder.taskDescription.setText(mTasks.get(position).getTaskDescription());
        holder.taskClass.setText(mTasks.get(position).getTaskClass());
        holder.taskPriority.setText("Priorità: " + mTasks.get(position).getTaskPriority());

        // data nel formato dd/M/yyyy
        String mTaskDue = DateFormat.getDateInstance(DateFormat.FULL).format(mTasks.get(position).getTaskDue().getTime());
        holder.taskDue.setText(mTaskDue);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mTasks.get(position));
                Toast.makeText(mContext, mTasks.get(position).getTaskName(), Toast.LENGTH_SHORT).show();

                modifyDialog(position);

            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Metodo per rimozione task
                removeDialog(position);

                //true = il long click è gestito qui; false altrimenti
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        TextView taskDescription;
        TextView taskClass;
        TextView taskPriority;
        TextView taskDue;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskClass = itemView.findViewById(R.id.taskClass);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            taskDue = itemView.findViewById(R.id.taskDue);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }

    private void removeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Eliminazione Task");
        builder.setMessage("Sei sicuro di voler eliminare l'impegno selezionato?");
        builder.setCancelable(false);

        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "Hai premuto Sì", Toast.LENGTH_SHORT).show();
                mTasks.remove(position);

                fileHelper.writeData(mTasks);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void modifyDialog(final int position){

    }
}