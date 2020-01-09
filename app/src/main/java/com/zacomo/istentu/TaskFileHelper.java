package com.zacomo.istentu;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class TaskFileHelper {

    Context context;

    public TaskFileHelper(Context context){
        this.context = context;
    }


    public void writeData(ArrayList<Task> mTasks, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTasks);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Task> readData(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> mTasks = gson.fromJson(json, type);

        if (mTasks == null){
            mTasks = new ArrayList<>();
        }

        return mTasks;
    }

/*
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
*/

}
