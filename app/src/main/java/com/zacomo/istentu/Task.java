package com.zacomo.istentu;

import java.util.ArrayList;
import java.util.Calendar;

public class Task {

    private String taskName;
    private String taskDescription;
    private int taskPriority;
    private Calendar taskDue;
    private String taskClass;

    public Task(String taskName, String taskDescription, int taskPriority, Calendar taskDue, String taskClass){
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
    }

    public String getTaskName(){
        return taskName;
    }

    public int getTaskPriority(){
        return taskPriority;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }

    public boolean setTaskPriority(int taskPriority){

        boolean done = false;

        if (taskPriority > 0 && taskPriority < 6){
            this.taskPriority = taskPriority;
            done = true;
        }
        else
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

    public String getTaskClass(){
        return taskClass;
    }

    public void setTaskClass(String taskClass){
        this.taskClass = taskClass;
    }

}
