package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddDialog.AddDialogListener {

    private static final String TAG = "MainActivity";
    private ArrayList<String> mTaskName = new ArrayList<>();
    private FloatingActionButton addButton;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        addButton = findViewById(R.id.fabAdd);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(this, mTaskName);

        sampleText();

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

    @Override
    public void insertData(String taskName) {
        mTaskName.add(taskName);
        //richiamo questo metodo per aggiornare la recyclerView
        //initRecyclerView();
        adapter.notifyItemInserted(mTaskName.indexOf(taskName));
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerViewAdapter adapter){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void sampleText(){
        mTaskName.add("Linea1");
        mTaskName.add("Linea2");
        mTaskName.add("Linea3");
        mTaskName.add("Linea4");
        mTaskName.add("Linea5");
        mTaskName.add("Linea6");
        mTaskName.add("Linea7");
        mTaskName.add("Linea8");
        mTaskName.add("Linea9");
        mTaskName.add("Linea10");
        mTaskName.add("Linea11");
        mTaskName.add("Linea12");
        mTaskName.add("Linea13");
        mTaskName.add("Linea14");
        mTaskName.add("Linea15");
    }

}
