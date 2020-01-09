package com.zacomo.istentu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddDialog.AddDialogListener, NavigationView.OnNavigationItemSelectedListener, SortDialog.SortDialogListener {

    private static final String TAG = "MainActivity";
    private ArrayList<Task> mTasks;
    private FloatingActionButton addButton;

    private RecyclerViewAdapter adapter;

    private TaskFileHelper tasksFH;
    private StringFileHelper classesFH;

    private ArrayList<String> spinnerClasses;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private Bundle classBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate: started");

        tasksFH = new TaskFileHelper(this);
        classesFH = new StringFileHelper(this);

        spinnerClasses = classesFH.readData("ClassList");
        classBundle = new Bundle();
        classBundle.putStringArrayList("ClassList",spinnerClasses);

        mTasks = tasksFH.readData("TaskList");

        addButton = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        adapter = new RecyclerViewAdapter(this, mTasks, tasksFH, MainActivity.this);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.mainActivity);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.drawerOpen,R.string.drawerClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

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
        addDialog.setArguments(classBundle);
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
        tasksFH.writeData(mTasks,"TaskList");
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
        addDialog.setArguments(classBundle);
        addDialog.setBundle(savedInstanceState);
        addDialog.show(getSupportFragmentManager(), "Modify dialog");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sort:
                Toast.makeText(this, "Sort Selected!", Toast.LENGTH_SHORT).show();
                openSortDialog();
                break;
            case R.id.addClass:
                Toast.makeText(this, "Add Class Selected!", Toast.LENGTH_SHORT).show();
                openAddClassDialog();
                break;
            case R.id.usageGraph:
                Toast.makeText(this, "Usage Graph Selected!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.info:
                Toast.makeText(this, "Info Selected!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }

    private void openSortDialog(){
        SortDialog sortDialog = new SortDialog();
        sortDialog.show(getSupportFragmentManager(),"sort_dialog");
    }

    private void openAddClassDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuova classe");
        builder.setMessage("Inserisci il nome di una nuova classe:");

        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newClass = editText.getText().toString().trim(); 
                //se la classe non è presente allora la aggiungo
                //!(spinnerClasses == null)
                if (!spinnerClasses.contains(newClass)){
                    spinnerClasses.add(newClass);
                    classesFH.writeData(spinnerClasses,"ClassList");
                }
                else
                    Toast.makeText(MainActivity.this, "Classe già presente!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sortByPriority(boolean ascendant) {
        Comparator<Task> comparator;

        if (ascendant) {
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    if (o1.getTaskPriority() < o2.getTaskPriority())
                        return -1;
                    if (o1.getTaskPriority() > o2.getTaskPriority())
                        return 1;

                    return 0;
                }
            };
        }
        else{
            //Con api >= 23 Collections.sort(mTasks, comparator.reversed());
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    if (o1.getTaskPriority() < o2.getTaskPriority())
                        return 1;
                    if (o1.getTaskPriority() > o2.getTaskPriority())
                        return -1;

                    return 0;
                }
            };
        }
        Collections.sort(mTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapter.notifyDataSetChanged();
    }

    private void sortByDate(boolean ascendant){
        Comparator<Task> comparator;

        if (ascendant) {
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    if (o1.getTaskDue().compareTo(o2.getTaskDue())<0)
                        return -1;
                    if (o1.getTaskDue().compareTo(o2.getTaskDue())>0)
                        return 1;

                    return 0;
                }
            };
        }
        else{
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    if (o1.getTaskDue().compareTo(o2.getTaskDue())<0)
                        return 1;
                    if (o1.getTaskDue().compareTo(o2.getTaskDue())>0)
                        return -1;

                    return 0;
                }
            };
        }
        Collections.sort(mTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapter.notifyDataSetChanged();
    }

    private void sortByName(boolean ascendant){
        Comparator<Task> comparator;

        if (ascendant) {
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return o1.getTaskName().compareTo(o2.getTaskName());
                }
            };
        }
        else{
            comparator = new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return (o1.getTaskName().compareTo(o2.getTaskName()))*-1;
                }
            };
        }

        Collections.sort(mTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void sortTasks(Integer sType, Integer sOrder) {
        //0 priority | 1 Date | 2 Name | 3 Class
        //0 Ascendant | 1 Descendant
        boolean ascendant = true;

        if (sOrder > 0)
            ascendant = false;

        switch (sType){
            case 0:
                //Sort by priority
                sortByPriority(ascendant);
                break;
            case 1:
                //Sort by date
                sortByDate(ascendant);
                break;
            case 2:
                //Sort by name
                sortByName(ascendant);
                break;
        }
    }
}
