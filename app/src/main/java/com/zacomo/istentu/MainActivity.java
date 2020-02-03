package com.zacomo.istentu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity implements AddDialog.AddDialogListener, NavigationView.OnNavigationItemSelectedListener, SortDialog.SortDialogListener {

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

    private RecyclerView recyclerView;

    private String doneText,cancelText,notCompletedText,completedText,allText;

    @Override
    // Qui gestisco le azioni da intraprendere quando si preme sui button di una notifica
    // intent contiene la l'extra "action" che con una stringa indica quale pulsante è stato premuto
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("action")){

            int position = intent.getIntExtra("position",-1);
            if (intent.getStringExtra("action").equals("setRunning") && position > -1){
                //chiamo metodo setRunning
                setRunning(mTasks.get(position));
            }
            else if (intent.getStringExtra("action").equals("setDone") && position > -1){
                //chiamo metodo setDone
                setDone(mTasks.get(position));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doneText = getString(R.string.done_text);
        cancelText = getString(R.string.cancel_text);
        notCompletedText = getString(R.string.mainActivity_filterDialog_notCompleted);
        completedText = getString(R.string.mainActivity_filterDialog_completed);
        allText = getString(R.string.mainActivity_filterDialog_all);

        //inizializzo i file helper che salvano i vettori task e classi nelle sharedPreferences
        tasksFH = new TaskFileHelper(this);
        classesFH = new StringFileHelper(this);

        spinnerClasses = classesFH.readData("ClassList");
        //classBundle = new Bundle();


        //classBundle.putStringArrayList("ClassList",spinnerClasses);
        mTasks = tasksFH.readData("TaskList");

        addButton = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);

        filteredTasks = new ArrayList<>();

        //inizializzo due adapter: uno per il task con tutti i vettori, l'altro con i vettori filtrati
        adapterAllTasks = new RecyclerViewAdapter(this, mTasks, tasksFH, MainActivity.this);
        adapterFilterTasks = new RecyclerViewAdapter(this, mTasks, filteredTasks, tasksFH, MainActivity.this);

        //inizializzazione del layout del drawer
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

        //in questo modo i task vengono visualizzati sempre in ordine di scadenza
        sortByDate(true);

        //inizializzazione recyclerview
        initRecyclerView(recyclerView, adapterAllTasks);

        //gestisco click sul fab per aggiungere una nuova attività
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addBtnDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //aggiorno la Recyclerview perchè potrebbe essere cambiata se è stata avviata l'activity Postpone
        mTasks = tasksFH.readData("TaskList");
        adapterAllTasks = new RecyclerViewAdapter(this, mTasks, tasksFH, MainActivity.this);
        initRecyclerView(recyclerView, adapterAllTasks);
    }

    //Questo metodo si occupa di gestire il dialog di aggiunta di un Task
    private void addBtnDialog() {

        //Questo bundle, con le classi, lo passo come argomento al dialogo di aggiunta/modifica task
        //in modo da popolare correttamente lo spinner delle classi
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ClassList",spinnerClasses);

        AddDialog addDialog = new AddDialog();
        addDialog.setArguments(bundle);
        addDialog.show(getSupportFragmentManager(), "Add dialog");

    }

    @Override
    //Implementazione del metodo dichiarato in AddDialog. Aggiunge un nuovo task o aggiorna quello modificato
    public void insertData(Task newTask) {

        if (newTask.getTaskPosition() != -1){
            // se è già presente, significa che potrebbe essere stato modificato

            //rimuovo il task precedente
            mTasks.remove(newTask.getTaskPosition());
            filterConsistency();

            adapterAllTasks.notifyItemRemoved(newTask.getTaskPosition());
            adapterFilterTasks.notifyDataSetChanged();

            //aggiorno la posizione del task modificato che sarà size()
            //newTask.setTaskPosition(mTasks.size());

            //aggiungo il task nella posizione in cui si trovava
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

        //se il task è nuovo, viene creata una nuova notifica che ha per id la posizione del task
        //se il task viene modificato, viene aggiornata la notifica (la data potrebbe essere cambiata)
        createAlarm(newTask.getTaskPosition());

        //aggiorno la recyclerView
        tasksFH.writeData(mTasks,"TaskList");
        adapterAllTasks.notifyItemInserted(mTasks.indexOf(newTask));
        adapterFilterTasks.notifyDataSetChanged();
        Toast.makeText(this, R.string.mainActivity_insertData_success, Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView(RecyclerView recyclerView, RecyclerViewAdapter adapter){
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //Apre, dopo il tap su un task, un dialogo in cui è possibile scegliere se modificare il task o cambiarne lo stato
    public void moreInfoDialog(final Task mTask){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //recupero la stringa "Cosa fare con" nella lingua corretta
        String whatTodo = getString(R.string.mainActivity_moreInfoDialog_title);
        builder.setTitle(whatTodo + "\""+mTask.getTaskName()+"\""+"?");
        builder.setCancelable(false);

        String modifyTask = getString(R.string.mainActivity_moreInfoDialog_ModifyTask);
        String setAsRunning = getString(R.string.mainActivity_moreInfoDialog_setAsRunning);
        String setAsDone = getString(R.string.mainActivity_moreInfoDialog_setAsDone);
        String setAsWaiting = getString(R.string.mainActivity_moreInfoDialog_setAsWaiting);

        builder.setItems(new CharSequence[]{modifyTask, setAsRunning, setAsDone, setAsWaiting}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position of the selected item
                        //status: 0 == in attesa | 1 == in corso | 2 == completo
                        switch (which) {
                            case 0:
                                modifyDialog(mTask);
                                break;
                            case 1:
                                setRunning(mTask);
                                break;
                            case 2:
                                setDone(mTask);
                                break;
                            case 3:
                                setWaiting(mTask);
                                break;
                        }
                    }
                });
        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    //Configura un bundle da passare come argomento per l'Add dialog in modo che sia un dialogo di modifica
    private void modifyDialog(Task mTask){

        Bundle bundle = new Bundle();
        bundle.putString("taskName", mTask.getTaskName());
        bundle.putString("taskDescription", mTask.getTaskDescription());
        bundle.putInt("taskPriority", mTask.getTaskPriority());
        bundle.putString("taskClass", mTask.getTaskClass());

        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(mTask.getTaskDue().getTime());
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(mTask.getTaskDue().getTime());

        bundle.putString("taskDueDate", date);
        bundle.putString("taskDueTime", time);

        //dovrebbe impostare come posizione del task la sua posizione in mTasks
        bundle.putInt("taskPosition", mTasks.indexOf(mTask));

        bundle.putStringArrayList("ClassList", spinnerClasses);

        AddDialog addDialog = new AddDialog();
        addDialog.setArguments(bundle);
        addDialog.show(getSupportFragmentManager(), "Modify dialog");
    }

    @Override
    //Gestisce il tap sulle voci della navigation bar laterale
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.sort:
                openSortDialog();
                break;
            case R.id.filter:
                openFilterDialog();
                break;
            case R.id.addClass:
                openAddClassDialog();
                break;
            case R.id.removeClass:
                openRemoveClassDialog();
                break;
            case R.id.notificationPreferences:
                openNotificationPreferencesDialog();
                break;
            case R.id.usageGraph:
                openUsageGraphActivity();
                break;
            case R.id.info:
                openInfoActivity();
            default:
                break;
        }
        navigationView.setCheckedItem(menuItem.getItemId());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Apre un dialog per ordinare i task della recyclerview
    private void openSortDialog(){
        SortDialog sortDialog = new SortDialog();
        sortDialog.show(getSupportFragmentManager(),"sort_dialog");
    }

    //Apre un dialog per filtrare i task della recyclerview
    private void openFilterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.mainActivity_filterDialog_title);
        builder.setTitle(title);
        String message = getString(R.string.mainActivity_filterDialog_message);
        builder.setMessage(message);

        final Spinner spinner = new Spinner(this);

        ArrayList<String> filterClasses = new ArrayList<>();
        if (spinnerClasses != null)
            filterClasses = new ArrayList<>(spinnerClasses);

        //Voglio poter filtrare anche secondo questi criteri, non solo la classe dei task
        filterClasses.add(notCompletedText);
        filterClasses.add(completedText);
        filterClasses.add(allText);

        ArrayAdapter<String> spinnerClassesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filterClasses);
        spinnerClassesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerClassesAdapter);
        builder.setView(spinner);

        builder.setPositiveButton(doneText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //Azioni per filtrare recyclerview
                filterRecycleView(spinner);
            }
        });

        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //Apre dialog per aggiungere una nuova classe, nome scelto dall'utente tramite EditText
    private void openAddClassDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.mainActivity_addClassDialog_title);
        String message = getString(R.string.mainActivity_addClassDialog_message);
        builder.setTitle(title);
        builder.setMessage(message);

        final EditText editText = new EditText(this);
        builder.setView(editText);

        builder.setPositiveButton(doneText, new DialogInterface.OnClickListener() {
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
                    Toast.makeText(MainActivity.this, R.string.mainActivity_addClassDialog_classAlreadyExists, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Dialog per rimuovere la classe selezionata (tramite spinner) dalla lista delle classi
    private void openRemoveClassDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.mainActivity_removeDialog_title);
        String message = getString(R.string.mainActivity_removeDialog_message);
        builder.setTitle(title);
        builder.setMessage(message);

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerClassesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerClasses);
        spinnerClassesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerClassesAdapter);
        builder.setView(spinner);

        builder.setPositiveButton(doneText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String newClass = editText.getText().toString().trim();
                spinnerClasses.remove(spinner.getSelectedItem().toString());
                classesFH.writeData(spinnerClasses,"ClassList");
            }
        });

        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Dialog per cambiare la preferenza delle notifiche (cioè la priorità necessaria per ricevere la notifica)
    private void openNotificationPreferencesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.mainActivity_notificationPreferencesDialog_title);
        String message = getString(R.string.mainActivity_notificationPreferencesDialog_message);
        builder.setTitle(title);
        builder.setMessage(message);

        final Spinner spinner = new Spinner(this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.priority_values));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        builder.setView(spinner);

        builder.setPositiveButton(doneText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //passo priorità selezionata (ovvero posizione + 1 perchè position parte da 0)
                filterAlarms(spinner.getSelectedItemPosition()+1);
            }
        });

        builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    //Da qui chiamo il metodo di ordinamento richiesto, in base al valore di sType
    public void sortTasks(Integer sType, Integer sOrder) {
        //0 priority | 1 Date | 2 Name | 3 Class
        //0 Ascendente | 1 Discendente
        boolean ascendant = true;

        if (sOrder > 0)
            ascendant = false;

        switch (sType){
            case 0:
                sortByPriority(ascendant);
                break;
            case 1:
                sortByDate(ascendant);
                break;
            case 2:
                sortByName(ascendant);
                break;
        }
    }

    //Ordino i task nella recyclerview per priorità ascendente o discendente (in base al valore di ascendant)
    private void sortByPriority(boolean ascendant) {
        Comparator<Task> comparator;

        //Definisco il comportamento del comparator in base al valore di ascendant

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

    //Ordino i task nella recyclerview per data di scadenza, da quella più vicina o da quella più lontana (in base ad ascendant)
    private void sortByDate(boolean ascendant){
        Comparator<Task> comparator;

        //Definisco il comportamento del comparator in base al valore di ascendant

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

    //Ordino i task nella recyclerview per Nome (in base al valore di ascendant)
    private void sortByName(boolean ascendant){
        Comparator<Task> comparator;

        //Definisco il comportamento del comparator in base al valore di ascendant

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

    //Filtro la recyclerview in base all'opzione scelta nel dialogo che ha chiamato il metodo
    private void filterRecycleView(Spinner spinner){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //resetto il vettore filteredTasks altrimenti compaiono anche task selezionati in precedenza
        filteredTasks.clear();

        //Se l'elemento selezionato non è uno degli ultimi 3 (ovvero non è uno tra "non completati", "completati" e "tutti"
        if (spinner.getSelectedItemPosition() < spinner.getCount() - 3){

            for (int i = 0; i < mTasks.size(); i++){
                //se la classe corrisponde alla classe selezionata allora aggiungo il task ai task filtrati
                if (mTasks.get(i).getTaskClass().equals(spinner.getSelectedItem().toString().trim()))
                    filteredTasks.add(mTasks.get(i));
            }
            initRecyclerView(recyclerView,adapterFilterTasks);
        }
        //altrimenti devo filtrare in base alla scelta
        else{
            //3 non completati, 2 completati, 1 tutti
            //nota che l'ultima posizione è sempre minore di 1 di getCount
            int id = spinner.getCount() - spinner.getSelectedItemPosition();
            switch (id){
                case 1: //tutti
                    //semplicemente inizializzo la recyclerview con l'adapter di tutti i task (mTasks)
                    initRecyclerView(recyclerView,adapterAllTasks);
                    break;
                case 2: //completati
                    for (int i = 0; i < mTasks.size(); i++){
                        if (mTasks.get(i).getTaskStatus() == 2)
                            filteredTasks.add(mTasks.get(i));
                    }

                    initRecyclerView(recyclerView,adapterFilterTasks);
                    break;
                case 3: //non completati
                    for (int i = 0; i < mTasks.size(); i++){
                        //status == 2 indica task completati
                        if (mTasks.get(i).getTaskStatus() != 2)
                            filteredTasks.add(mTasks.get(i));
                    }

                    initRecyclerView(recyclerView,adapterFilterTasks);
                    break;
            }
        }
    }

    //Rimuove da filtered tasks gli elementi rimossi da mTasks, in modo che la view filtrata sia sempre aggiornata
    private void filterConsistency(){
        int i = 0;
        while (i < filteredTasks.size()){
            if (!mTasks.contains(filteredTasks.get(i)))
                filteredTasks.remove(i);
            i++;
        }
    }

    //Imposta lo stato di un task come "in corso"
    private void setRunning(Task mTask){
        cancelAlarm(mTask.getTaskPosition());
        mTask.setStatus(1);
        mTask.setDoneDate(null);
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();

        tasksFH.writeData(mTasks,"TaskList");
    }

    //Imposta lo stato di un task come "Completato"
    private void setDone(Task mTask){
        cancelAlarm(mTask.getTaskPosition());
        mTask.setStatus(2);
        mTask.setDoneDate(Calendar.getInstance());
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();

        tasksFH.writeData(mTasks,"TaskList");
    }

    //Imposta lo stato di un task come "in attesa"
    private void setWaiting(Task mTask){
        mTask.setStatus(0);
        mTask.setDoneDate(null);
        adapterAllTasks.notifyDataSetChanged();
        adapterFilterTasks.notifyDataSetChanged();

        tasksFH.writeData(mTasks,"TaskList");
    }

    //Crea un alarm (notifica con scadenza) per il task che ha posizione "position" in mTasks
    private void createAlarm(int position){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);

        //Passo la posizione del task al receiver
        intent.putExtra("position",position);

        //Passo il nome del task al receiver
        intent.putExtra("name",mTasks.get(position).getTaskName());

        //Creo il pendingIntent che fa da wrapper per "intent"
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, position, intent, 0);

        if (alarmManager!= null)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTasks.get(position).getTaskDue().getTimeInMillis(), pendingIntent);

    }

    //Data la posizione in mTasks, cancella l'alarm per il task corrispondente
    public void cancelAlarm(int position){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, position, intent, 0);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
   }

   //Fa in modo che siano attivi solo gli alarm della priorità selezionata
   public void filterAlarms(int priority){
        //affinchè il filtro ripristini gli alarm cancellati quando si passa da una priorità più alta
        //a una più bassa, occorre usare anche createAlarm, anche se per alcuni task non serve
       if (priority > -1 && priority < 6){
            for (int i = 0; i < mTasks.size(); i++) {
                if (mTasks.get(i).getTaskPriority() < priority)
                    cancelAlarm(i);
                else
                    createAlarm(i);
            }
        }

   }

   //Apre l'activity in cui viene mostrato il grafico di utilizzo
   public void openUsageGraphActivity(){
        Intent intent = new Intent(this, UsageGraphActivity.class);
        startActivity(intent);
   }

   //Apre l'activity in cui vengono mostrate alcune informazioni sull'app
    public void openInfoActivity(){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}