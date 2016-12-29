package koki_kambic.emp_project;

import android.content.Context;

/**
 * Created by koki on 21.12.2016.
 */

public class TaskModel {
    String taskID;
    String userID;
    String taskName;
    String days;
    String hours;
    Context context;
    public TaskModel(String taskID, String taskName, String [] time,Context context,String userId) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.days = time[0];
        this.hours = time[1];
        this.context = context;
        this.userID = userId;
    }

    public String getTaskName() {
        return taskName;
    }
    public String getTaskID(){
        return taskID;
    }
    public String getUserID(){
        return userID;
    }
    public String getTaskDays(){
        return days;
    }
    public String getTaskHours(){
        return hours;
    }
    public Context getContext(){
        return context;
    }

}
