package com.zacomo.istentu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<Task> mTasks;
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<Task> tasks) {
        this.mTasks = tasks;
        this.mContext = context;
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
        holder.taskPriority.setText("Priorità: " + mTasks.get(position).getTaskPriority());

        // data nel formato dd/M/yyyy
        String mTaskDue = DateFormat.getDateInstance(DateFormat.FULL).format(mTasks.get(position).getTaskDue().getTime());
        holder.taskDue.setText(mTaskDue);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mTasks.get(position));

                Toast.makeText(mContext, mTasks.get(position).getTaskName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                Log.d(TAG, "onLongClick: clicked on: " + mTasks.get(position));

                Toast.makeText(mContext, "LOOONGPRESS", Toast.LENGTH_SHORT).show();
                //ToDO: implementare eliminazione task con dialog di conferma
                //true = il long click è gestito qui; false altrimenti
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView taskName;
        TextView taskDescription;
        TextView taskPriority;
        TextView taskDue;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            taskDue = itemView.findViewById(R.id.taskDue);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }
}
