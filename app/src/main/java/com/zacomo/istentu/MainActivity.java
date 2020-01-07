package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddDialog.AddDialogListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Task> mTasks;
    private FloatingActionButton addButton;

    private RecyclerViewAdapter adapter;

    private FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        fileHelper = new FileHelper(this);

        mTasks = fileHelper.readData();
        addButton = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new RecyclerViewAdapter(this, mTasks, fileHelper, MainActivity.this);

        //sampleText();

        initRecyclerView(recyclerView, adapter);

        addButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //pressing add floating button
            case R.id.fabAdd:
                addBtnDialog();
                break;
        }
    }

    private void addBtnDialog() {

        AddDialog addDialog = new AddDialog();
        addDialog.show(getSupportFragmentManager(), "Add dialog");

    }

    //metodo che inserisce nel vettore il task (nuovo o modificato che sia)
    @Override
    public void insertData(Task newTask) {

        if (newTask.getTaskPosition() != -1){
            // se è già presente, significa che potrebbe essere stato modificato

            //rimuovo il task precedente
            mTasks.remove(newTask.getTaskPosition());

            adapter.notifyItemRemoved(newTask.getTaskPosition());

            Toast.makeText(this, String.valueOf(newTask.getTaskPosition()), Toast.LENGTH_SHORT).show();
            //aggiorno la posizione del task modificato che sarà size()
            //newTask.setTaskPosition(mTasks.size());

            //aggiungo il task
            mTasks.add(newTask.getTaskPosition(), newTask);
        }
        else{
            //se ho un nuovo task

            //aggiorno la posizione del nuovo task, pari a size perchè aggiunto in fondo
            newTask.setTaskPosition(mTasks.size());

            mTasks.add(newTask);
        }

        //aggiorno la recyclerView
        fileHelper.writeData(mTasks);
        adapter.notifyItemInserted(mTasks.indexOf(newTask));
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerViewAdapter adapter){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void modifyDialog(final int position){

        //ToDo: si potrebbe sostituire tutto con una stringa Json

        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putString("taskName", mTasks.get(position).getTaskName());
        savedInstanceState.putString("taskDescription", mTasks.get(position).getTaskDescription());
        savedInstanceState.putInt("taskPriority", mTasks.get(position).getTaskPriority());
        savedInstanceState.putString("taskClass", mTasks.get(position).getTaskClass());

        int month = mTasks.get(position).getTaskDue().getInstance().get(Calendar.MONTH);
        month++;
        String date = mTasks.get(position).getTaskDue().getInstance().get(Calendar.YEAR) + "/" + month
                    + "/" + mTasks.get(position).getTaskDue().getInstance().get(Calendar.DAY_OF_MONTH);

        savedInstanceState.putString("taskDue", date);
        savedInstanceState.putInt("taskPosition", position);
        AddDialog addDialog = new AddDialog();
        addDialog.setBundle(savedInstanceState);
        addDialog.show(getSupportFragmentManager(), "Modify dialog");
    }

    private void sampleText(){
        Calendar dataProva = new Calendar() {
            @Override
            protected void computeTime() {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            public void add(int field, int amount) {

            }

            @Override
            public void roll(int field, boolean up) {

            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }
        };
        dataProva.set(2020,5,17,12,0, 0);

        mTasks.add(new Task("Fare la spesa", "- Latte\n- Uova\n- Biscotti",3, dataProva, "Categoria A"));
        mTasks.add(new Task("Comprare Libro", "- Origin Dan Brown",3, dataProva, "Categoria B"));
        mTasks.add(new Task("Studiare Algoritmi", "- Strutture Dati \n- Algoritmi",3, dataProva, "Categoria B"));
        mTasks.add(new Task("Tirare barduffula", "- Stringere forte il filo e lanciare",3, dataProva, "Categoria A"));
        mTasks.add(new Task("Affitto", "- Pagare mese di Gennaio",3, dataProva, "Categoria A"));
    }

}
