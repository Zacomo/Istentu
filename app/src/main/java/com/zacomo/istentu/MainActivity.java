package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<String> mNomeTask = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        sampleText();
        initRecyclerView();
    }


    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNomeTask);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void sampleText(){
        mNomeTask.add("Linea1");
        mNomeTask.add("Linea2");
        mNomeTask.add("Linea3");
        mNomeTask.add("Linea4");
        mNomeTask.add("Linea5");
        mNomeTask.add("Linea6");
        mNomeTask.add("Linea7");
        mNomeTask.add("Linea8");
        mNomeTask.add("Linea9");
        mNomeTask.add("Linea10");
        mNomeTask.add("Linea11");
        mNomeTask.add("Linea12");
        mNomeTask.add("Linea13");
        mNomeTask.add("Linea14");
        mNomeTask.add("Linea15");
        mNomeTask.add("Linea16");
        mNomeTask.add("Linea17");
        mNomeTask.add("Linea18");
        mNomeTask.add("Linea19");
        mNomeTask.add("Linea20");
        mNomeTask.add("Linea21");
        mNomeTask.add("Linea22");
        mNomeTask.add("Linea23");
        mNomeTask.add("Linea24");
        mNomeTask.add("Linea25");
        mNomeTask.add("Linea26");
        mNomeTask.add("Linea27");
        mNomeTask.add("Linea28");
        mNomeTask.add("Linea29");
        mNomeTask.add("Linea30");
        mNomeTask.add("Linea31");
        mNomeTask.add("Linea32");
        mNomeTask.add("Linea33");
        mNomeTask.add("Linea34");
        mNomeTask.add("Linea35");
        mNomeTask.add("Linea36");
    }


}
