package com.zacomo.istentu;

import android.icu.text.TimeZoneFormat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Task {

    private String taskName;
    private String taskDescription;
    private int taskPriority;
    private Calendar taskDue;
    private String taskClass;
    private int taskPosition;

    private int status;
    private Calendar doneDate;

    public Task(){

    }

    public Task(String taskName, String taskDescription, int taskPriority, Calendar taskDue, String taskClass) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskClass = taskClass;
        //La priorità sarà da 1 a 5
        if (taskPriority > 0 && taskPriority < 6)
            this.taskPriority = taskPriority;
        else
            this.taskPriority = 0;

        //La data non può essere precedente ad oggi o nulla; se succede, viene impostata la data odierna
        if (taskDue == null || taskDue.getTime().before(Calendar.getInstance().getTime()))
            this.taskDue = Calendar.getInstance();
        else
            this.taskDue = taskDue;

        taskPosition = -1;
        status = 0;
        doneDate = null;
   }

    public String getTaskName() {
        return taskName;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean setTaskPriority(int taskPriority) {

        boolean done = false;

        if (taskPriority > 0 && taskPriority < 6) {
            this.taskPriority = taskPriority;
            done = true;
        } else
            this.taskPriority = 0;
        return done;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }


    public Calendar getTaskDue() {
        return taskDue;
    }

    public void setTaskDue(Calendar taskDue) {
        if (taskDue.before(Calendar.getInstance().getTime()))
            this.taskDue = Calendar.getInstance();
        else
            this.taskDue = taskDue;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }


    public int getTaskPosition() {
        return taskPosition;
    }

    public void setTaskPosition(int position) {
        taskPosition = position;
    }

    public int getTaskStatus() {
        return status;
    }

    public String statusToString(){
        String statusString = "Stato: ";
        switch (status){
            case 0:
                statusString += "in attesa";
                break;
            case 1:
                statusString += "in corso";
                break;
            case 2:
                String date = "";
                String time = "";
                String prova = "";
                if (doneDate != null){
                    date = DateFormat.getDateInstance(DateFormat.SHORT).format(doneDate.getTime());
                    time = DateFormat.getTimeInstance(DateFormat.SHORT).format(doneDate.getTime());
                }
                statusString = "Completato in data\n" + date + " alle " + time + "\n" + prova;
                break;
        }
        return statusString;
    }

    public String taskDueToString(){
        String date, time;
        date = DateFormat.getDateInstance(DateFormat.SHORT).format(taskDue.getTime());
        time = DateFormat.getTimeInstance(DateFormat.SHORT).format(taskDue.getTime());
        return "Scadenza: \n" + date + " alle " + time;
    }

    public void setStatus(int status) {
        //dev'essere compreso tra 0 e 2
        if (status < 0 || status > 2)
            this.status = 0;
        else
            this.status = status;
    }

    public Calendar getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Calendar doneDate) {
        this.doneDate = doneDate;
    }
}