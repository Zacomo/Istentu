package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddDialog.AddDialogListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Task> mTasks;
    private FloatingActionButton addButton;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        readData();
        addButton = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new RecyclerViewAdapter(this, mTasks);

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

    private void writeData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTasks);
        editor.putString("TaskList", json);
        editor.apply();
    }

    private void readData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("TaskList", null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        mTasks = gson.fromJson(json, type);

        if (mTasks == null){
            mTasks = new ArrayList<>();
        }
    }

    //metodo che con i valori del dialog crea un nuovo task e lo inserisce nel vettore
    @Override
    public void insertData(String taskName, String taskDescription, int taskPriority, Calendar taskDue) {
        Task newTask = new Task(taskName, taskDescription, taskPriority, taskDue);
        mTasks.add(newTask);

        //aggiorno la recyclerView
        writeData();
        adapter.notifyItemInserted(mTasks.indexOf(newTask));
        Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerViewAdapter adapter){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        mTasks.add(new Task("Fare la spesa", "- Latte\n- Uova\n- Biscotti",3, dataProva));
        mTasks.add(new Task("Comprare Libro", "- Origin Dan Brown",3, dataProva));
        mTasks.add(new Task("Studiare Algoritmi", "- Strutture Dati \n- Algoritmi",3, dataProva));
        mTasks.add(new Task("Tirare barduffula", "- Stringere forte il filo e lanciare",3, dataProva));
        mTasks.add(new Task("Affitto", "- Pagare mese di Gennaio",3, dataProva));
    }

}
