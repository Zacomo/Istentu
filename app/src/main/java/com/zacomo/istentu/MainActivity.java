package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ArrayList<String> mTaskName = new ArrayList<>();
    private FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        addButton = findViewById(R.id.fabAdd);

        sampleText();
        initRecyclerView();

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

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mTaskName);
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
        mTaskName.add("Linea16");
        mTaskName.add("Linea17");
        mTaskName.add("Linea18");
        mTaskName.add("Linea19");
        mTaskName.add("Linea20");
        mTaskName.add("Linea21");
        mTaskName.add("Linea22");
        mTaskName.add("Linea23");
        mTaskName.add("Linea24");
        mTaskName.add("Linea25");
        mTaskName.add("Linea26");
        mTaskName.add("Linea27");
        mTaskName.add("Linea28");
        mTaskName.add("Linea29");
        mTaskName.add("Linea30");
        mTaskName.add("Linea31");
        mTaskName.add("Linea32");
        mTaskName.add("Linea33");
        mTaskName.add("Linea34");
        mTaskName.add("Linea35");
        mTaskName.add("Linea36");
    }

}
