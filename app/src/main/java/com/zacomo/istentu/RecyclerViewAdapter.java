package com.zacomo.istentu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

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

        holder.taskName.setText(recViewTasks.get(position).getTaskName());
        holder.taskDescription.setText(recViewTasks.get(position).getTaskDescription());
        holder.taskClass.setText(recViewTasks.get(position).getTaskClass());

        String priorityText = mContext.getString(R.string.recyclerViewAdapter_priority_text);
        String txt = priorityText + " " + recViewTasks.get(position).getTaskPriority();
        holder.taskPriority.setText(txt);

        // data nel formato dd/M/yyyy
        holder.taskDue.setText(getTaskDueText(recViewTasks.get(position)));

        holder.taskStatus.setText(getTaskStatusText(recViewTasks.get(position)));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.moreInfoDialog(recViewTasks.get(position));

            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Metodo per rimozione task
                if (tasksFH != null)
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

    //invoca un dialog che chiede conferma per l'eliminazione di un task
    private void removeDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String removeDialogTitle = mContext.getString(R.string.recyclerViewAdapter_removeDialog_title);
        String removeDialogMessage = mContext.getString(R.string.recyclerViewAdapter_removeDialog_message);
        builder.setTitle(removeDialogTitle);
        builder.setMessage(removeDialogMessage+ "\"" + recViewTasks.get(position).getTaskName() + "\"?");
        builder.setCancelable(false);

        String affirmative = mContext.getString(R.string.affirmative_text);

        builder.setPositiveButton(affirmative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tasksFH!=null){

                    //elimino la notifica per il task che cancello
                    mainActivity.cancelAlarm(position);

                    String doneText = mContext.getString(R.string.done_text);
                    Toast.makeText(mContext, doneText, Toast.LENGTH_SHORT).show();
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

        String negative = mContext.getString(R.string.negative_text);
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private String getTaskStatusText(Task mTask){
        String statusText = mContext.getString(R.string.recyclerViewAdapter_taskStatusText_status) + " ";

        switch (mTask.getTaskStatus()){
            case 0:
                statusText += mContext.getString(R.string.recyclerViewAdapter_taskStatusText_waiting);
                break;
            case 1:
                statusText += mContext.getString(R.string.recyclerViewAdapter_taskStatusText_running);
                break;
            case 2:
                String date = "";
                String time = "";
                if (mTask.getDoneDate() != null){
                    date = DateFormat.getDateInstance(DateFormat.SHORT).format(mTask.getDoneDate().getTime());
                    time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mTask.getDoneDate().getTime());
                }
                String completedOn = mContext.getString(R.string.recyclerViewAdapter_taskStatusText_completedOn);
                String atText = mContext.getString(R.string.recyclerViewAdapter_taskStatusText_completedAt);
                statusText = completedOn + " " + date + "\n" + atText + " " + time;
                break;
        }

        return statusText;
    }

    private String getTaskDueText(Task mTask){
        String date, time;
        date = DateFormat.getDateInstance(DateFormat.SHORT).format(mTask.getTaskDue().getTime());
        time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mTask.getTaskDue().getTime());
        String deadline = mContext.getString(R.string.recyclerViewAdapter_taskDueText_deadline);
        String atText = mContext.getString(R.string.recyclerViewAdapter_taskStatusText_completedAt);
        return deadline + " " + date + "\n" + atText + " " + time;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        TextView taskStatus;
        TextView taskDescription;
        TextView taskClass;
        TextView taskPriority;
        TextView taskDue;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.taskName);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskClass = itemView.findViewById(R.id.taskClass);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            taskDue = itemView.findViewById(R.id.taskDue);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }
}