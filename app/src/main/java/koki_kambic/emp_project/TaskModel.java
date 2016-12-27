package koki_kambic.emp_project;

/**
 * Created by koki on 21.12.2016.
 */

public class TaskModel {
    String taskID;
    String taskName;

    public TaskModel(String taskID, String taskName) {
        this.taskID = taskID;
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
    public String getTaskID(){
        return taskID;
    }

}
