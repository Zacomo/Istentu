package com.zacomo.istentu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    private TextView repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        repository = findViewById(R.id.infoActivityRepository);
        repository.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
