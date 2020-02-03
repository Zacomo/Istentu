package com.zacomo.istentu;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class StringFileHelper {
    Context context;

    public StringFileHelper(Context context){
        this.context = context;
    }

    //Converte strings in json e la salva nelle shared preferences con chiave key
    public void writeData(ArrayList<String> strings, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(strings);
        editor.putString(key, json);
        editor.apply();
    }

    //restituisce l'arraylist corrispondente alla chiave key
    public ArrayList<String> readData(String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> strings = gson.fromJson(json, type);

        if (strings == null){
            strings = new ArrayList<>();
        }

        return strings;
    }

}
