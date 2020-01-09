package com.zacomo.istentu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<Task> mTasks;
    private Context mContext;
    private MainActivity mainActivity;

    private ArrayList<Task> recViewTasks;

    private TaskFileHelper tasksFH;

    public RecyclerViewAdapter(Context context, ArrayList<Task> tasks, ArrayList<Task> filterTasks, TaskFileHelper tasksFH, MainActivity mainActivity) {
        this.mContext = context;
        this.mTasks = tasks;
        recViewTasks = filterTasks;
        this.tasksFH = tasksFH;
        this.mainActivity = mainActivity;
    }

    public RecyclerViewAdapter(Context context, ArrayList<Task> tasks, TaskFileHelper tasksFH, MainActivity mainActivity) {
        this.mContext = context;
        this.mTasks = tasks;
        recViewTasks = mTasks;
        this.tasksFH = tasksFH;
        this.mainActivity = mainActivity;
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

        holder.taskName.setText(recViewTasks.get(position).getTaskName());
        holder.taskDescription.setText(recViewTasks.get(position).getTaskDescription());
        holder.taskClass.setText(recViewTasks.get(position).getTaskClass());
        holder.taskPriority.setText("Priorità: " + recViewTasks.get(position).getTaskPriority());

        // data nel formato dd/M/yyyy
        holder.taskDue.setText(recViewTasks.get(position).taskDueToString());

        holder.taskDone.setText(recViewTasks.get(position).statusToString());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + recViewTasks.get(position));
                //Toast.makeText(mContext, recViewTasks.get(position).getTaskPosition() + "||" + position, Toast.LENGTH_SHORT).show();
                mainActivity.moreInfoDialog(recViewTasks.get(position));
                //mainActivity.modifyDialog(recViewTasks.get(position));

            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Metodo per rimozione task
                if (tasksFH!= null)
                    removeDialog(position);

                //true = il long click è gestito qui; false altrimenti
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return recViewTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        TextView taskDone;
        TextView taskDescription;
        TextView taskClass;
        TextView taskPriority;
        TextView taskDue;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.taskName);
            taskDone = itemView.findViewById(R.id.taskDone);
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
        builder.setMessage("Sei sicuro di voler eliminare " + "\"" + recViewTasks.get(position).getTaskName() + "\"");
        builder.setCancelable(false);

        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tasksFH!=null){

                    Toast.makeText(mContext, "Fatto!", Toast.LENGTH_SHORT).show();
                    Task toRemove = recViewTasks.get(position);
                    recViewTasks.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                    //devo aggiornare le posizioni dei task successivi a quello rimosso
                    for (int i = position; i < recViewTasks.size(); i++)
                        recViewTasks.get(i).setTaskPosition(i);

                    //Rimuovere elemento rimosso da recViewTasks anche da mTasks
                    mTasks.remove(toRemove);

                    //salvo mTasks, non recViewTasks
                    tasksFH.writeData(mTasks,"TaskList");
                }
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
}