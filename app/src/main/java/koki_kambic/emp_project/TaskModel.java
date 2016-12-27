package koki_kambic.emp_project;

/**
 * Created by koki on 21.12.2016.
 */

public class TaskModel {
    String taskID;
    String taskName;
    String days;
    String hours;
    public TaskModel(String taskID, String taskName, String [] time) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.days = time[0];
        this.hours = time[1];
    }

    public String getTaskName() {
        return taskName;
    }
    public String getTaskID(){
        return taskID;
    }
    public String getTaskDays(){
        return days;
    }
    public String getTaskHours(){
        return hours;
    }

}
