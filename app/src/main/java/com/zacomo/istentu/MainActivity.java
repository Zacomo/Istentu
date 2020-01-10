package com.zacomo.istentu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static com.zacomo.istentu.BaseApp.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddDialog.AddDialogListener, NavigationView.OnNavigationItemSelectedListener, SortDialog.SortDialogListener {

    //NotificationManagerCompat utilizza dei controlli per garantire la retrocompatibilità delle notifiche
    private NotificationManagerCompat notificationManager;

    private static final String TAG = "MainActivity";
    private ArrayList<Task> mTasks;
    private FloatingActionButton addButton;

    private RecyclerViewAdapter adapterAllTasks;
    private RecyclerViewAdapter adapterFilterTasks;

    private ArrayList<Task> filteredTasks;

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

        notificationManager = NotificationManagerCompat.from(this);

        tasksFH = new TaskFileHelper(this);
        classesFH = new StringFileHelper(this);

        spinnerClasses = classesFH.readData("ClassList");
        classBundle = new Bundle();

        classBundle.putStringArrayList("ClassList",spinnerClasses);
        mTasks = tasksFH.readData("TaskList");

        addButton = findViewById(R.id.fabAdd);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        filteredTasks = new ArrayList<>();

        adapterAllTasks = new RecyclerViewAdapter(this, mTasks, tasksFH, MainActivity.this);
        adapterFilterTasks = new RecyclerViewAdapter(this, mTasks, filteredTasks, tasksFH, MainActivity.this);

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

        initRecyclerView(recyclerView, adapterAllTasks);

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
            filterConsistency();

            adapterAllTasks.notifyItemRemoved(newTask.getTaskPosition());
            adapterFilterTasks.notifyDataSetChanged();

            Toast.makeText(this, String.valueOf(newTask.getTaskPosition()), Toast.LENGTH_SHORT).show();
            //aggiorno la posizione del task modificato che sarà size()
            //newTask.setTaskPosition(mTasks.size());

            //aggiungo il task
            mTasks.add(newTask.getTaskPosition(), newTask);
            if (filteredTasks.size() > 0 && (filteredTasks.get(0).getTaskClass().equals(newTask.getTaskClass())))
                filteredTasks.add(newTask);
        }
        else{
            //se ho un nuovo task

            //aggiorno la posizione del nuovo task, pari a size perchè aggiunto in fondo
            newTask.setTaskPosition(mTasks.size());

            mTasks.add(newTask);
            if (filteredTasks.size() > 0 && (filteredTasks.get(0).getTaskClass().equals(newTask.getTaskClass())))
                filteredTasks.add(newTask);
        }

        //aggiorno la recyclerView
        tasksFH.writeData(mTasks,"TaskList");
        adapterAllTasks.notifyItemInserted(mTasks.indexOf(newTask));
        adapterFilterTasks.notifyDataSetChanged();
        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerViewAdapter adapter){
        Log.d(TAG, "initRecyclerView: initRecyclerView");

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void moreInfoDialog(final Task mTask){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(mTask.getTaskName());
        builder.setTitle("Cosa vuoi fare con "+ "\""+mTask.getTaskName()+"\""+"?");
        builder.setCancelable(false);

        builder.setItems(new CharSequence[]{"Modifica Task", "Segna come \"in corso\"", "Segna come \"completo\"","Segna come \"in attesa\""}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position of the selected item
                        //status: 0 == in attesa | 1 == in corso | 2 == completo
                        switch (which) {
                            case 0:
                                modifyDialog(mTask);
                                break;
                            case 1:
                                mTask.setStatus(1);
                                mTask.setDoneDate(null);
                                adapterAllTasks.notifyDataSetChanged();
                                adapterFilterTasks.notifyDataSetChanged();
                                break;
                            case 2:
                                mTask.setStatus(2);
                                mTask.setDoneDate(Calendar.getInstance());
                                adapterAllTasks.notifyDataSetChanged();
                                adapterFilterTasks.notifyDataSetChanged();
                                break;
                            case 3:
                                mTask.setStatus(0);
                                mTask.setDoneDate(null);
                                adapterAllTasks.notifyDataSetChanged();
                                adapterFilterTasks.notifyDataSetChanged();
                                break;
                        }
                        tasksFH.writeData(mTasks,"TaskList");
                    }
                });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    //ora può essere private
    public void modifyDialog(Task mTask){

        //ToDo: si potrebbe sostituire tutto con una stringa Json

        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putString("taskName", mTask.getTaskName());
        savedInstanceState.putString("taskDescription", mTask.getTaskDescription());
        savedInstanceState.putInt("taskPriority", mTask.getTaskPriority());
        savedInstanceState.putString("taskClass", mTask.getTaskClass());

        int month = mTask.getTaskDue().getInstance().get(Calendar.MONTH);
        month++;
        String date = mTask.getTaskDue().getInstance().get(Calendar.YEAR) + "/" + month
                    + "/" + mTask.getTaskDue().getInstance().get(Calendar.DAY_OF_MONTH);

        savedInstanceState.putString("taskDue", date);

        //dovrebbe impostare come posizione del task la sua posizione in mtasks
        savedInstanceState.putInt("taskPosition", mTasks.indexOf(mTask));

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
            case R.id.filter:
                Toast.makeText(this, "Filter Selected!", Toast.LENGTH_SHORT).show();
                openFilterDialog();
                break;
            case R.id.addClass:
                Toast.makeText(this, "Add Class Selected!", Toast.LENGTH_SHORT).show();
                openAddClassDialog();
                break;
            case R.id.removeClass:
                Toast.makeText(this, "Remove Class Selected!", Toast.LENGTH_SHORT).show();
                openRemoveClassDialog();
                break;
            case R.id.notificationPreferences:
                Toast.makeText(this, "Notification Preferences Selected!", Toast.LENGTH_SHORT).show();
                //potrei estrapolare testo task e passarlo come parametro di sendOnChannel1
                sendOnChannel1(findViewById(R.id.drawerLayout));
                //openNotificationPreferencesDialog();
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

    private void openFilterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtra per classe");
        builder.setMessage("Scegli la classe da visualizzare");

        final Spinner spinner = new Spinner(this);

        ArrayList<String> filterClasses = new ArrayList<>();
        if (spinnerClasses != null)
            filterClasses = (ArrayList<String>) spinnerClasses.clone();

        filterClasses.add("Tutte");

        Toast.makeText(this, filterClasses.toString(), Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> spinnerClassesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterClasses);
        spinnerClassesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerClassesAdapter);
        builder.setView(spinner);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //Azioni per filtrare recyclerview, da implementare anche caso "Tutte"
                filterRecycleView(spinner.getSelectedItem().toString().trim());
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

    private void openRemoveClassDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rimuovi classe");
        builder.setMessage("Scegli la classe da rimuovere:");

        final Spinner spinner = new Spinner(this);
        Toast.makeText(this, spinnerClasses.toString(), Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> spinnerClassesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerClasses);
        spinnerClassesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerClassesAdapter);
        builder.setView(spinner);

        builder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String newClass = editText.getText().toString().trim();
                spinnerClasses.remove(spinner.getSelectedItem().toString());
                classesFH.writeData(spinnerClasses,"ClassList");
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

    private void openNotificationPreferencesDialog(){
        
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
        Collections.sort(filteredTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();
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
        Collections.sort(filteredTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();
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
        Collections.sort(filteredTasks, comparator);
        tasksFH.writeData(mTasks,"TaskList");
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();
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

    private void filterRecycleView(String filterClass){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //resetto il vettore filteredTasks altrimenti compaiono anche task selezionati in precedenza
        filteredTasks.clear();

        if (!filterClass.equals("Tutte")){

            for (int i = 0; i < mTasks.size(); i++){
                if (mTasks.get(i).getTaskClass().equals(filterClass))
                    filteredTasks.add(mTasks.get(i));
            }

            initRecyclerView(recyclerView,adapterFilterTasks);

        }

        else{
            Toast.makeText(this, "Tutte selected", Toast.LENGTH_SHORT).show();

            initRecyclerView(recyclerView,adapterAllTasks);
        }

        //filteredTasks = updateTaskPositions(filteredTasks);

    }

    private ArrayList<Task> updateTaskPositions(ArrayList<Task> tasks){
        for (int i=0; i<tasks.size(); i++)
            tasks.get(i).setTaskPosition(i);

        return tasks;
    }

    private void filterConsistency(){
        //metodo per rimuovere da filtered tasks gli elementi rimossi da mTasks
        for (int i=0; i < filteredTasks.size(); i++){
            if (!mTasks.contains(filteredTasks.get(i)))
                filteredTasks.remove(i);
        }
    }

    public void sendOnChannel1(View v){
        String title = "Nome Task Qui?";
        String message = "Nome Task scade oggi!";

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage",message);
        //FLAG_UPDATE_CURRENT indica che se viene creato un nuovo intent, il messaggio viene agggiornato
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0,broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activityIntent = new Intent(this, MainActivity.class);
        //wrapper per poter passare l'intent alla notifica
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent,0);

        //Si possono avere fino a 3 actionbutton (.addAction)
        //.setWhen e .setShowWhen
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_wb_incandescent_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_watch_later_black_24dp, "Toast", actionIntent)
                .build();
        //l'id dev'essere unico se voglio mandare più notifiche contemporaneamente da qui
        //se voglio cambiare o eliminare una notifica devo usare l'id corrispondente
        notificationManager.notify(1,notification);
    }
}