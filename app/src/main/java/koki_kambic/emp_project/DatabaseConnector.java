package koki_kambic.emp_project;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;


public class DatabaseConnector {

    private static final String DATABASE_NAME = "Data";
    private static final int DATABASE_VERSION = 4;
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    DatabaseConnector(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    void open() throws SQLException {
        //create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    }

    void close() {
        if (database != null) database.close();
    }

    //check if userId exist
    boolean ExistId(String id){
        Cursor cr = database.rawQuery("SELECT * FROM Users " +
                "WHERE id = '"+id+"';",null);
        int i =cr.getCount();
        cr.close();
        return (i != 0);
    }
    //check if local username exist
    boolean UserExist(String username){
         Cursor cr = database.rawQuery("SELECT * FROM LocalUsers " +
                "WHERE username = '"+username+"';",null);
         int i =cr.getCount();
         cr.close();
         return (i != 0);
    }
    //if password or/and username are incorrect then return false
    boolean CheckPassword(String username, String password){
        Cursor cr = database.rawQuery("SELECT * FROM LocalUsers " +
                "WHERE username = '"+username+"' " +
                "AND password = '"+password+"';",null);
        int i =cr.getCount();
        cr.close();
        return (i != 0);
    }
    //generate id for local users
    private String generateID(){
        int min = 10000000;
        int max = 999999999;
        Random r = new Random();
        return String.valueOf(r.nextInt((max - min) + 1) + min);
    }
    // insert User ID if ID does not exist in database
    void insertUserID(String id){
        if(!ExistId(id)) {
            String insertValuesUsers = "INSERT INTO Users (id) " +
                    "values('" + id + "')";
            database.execSQL(insertValuesUsers);
        }
    }
    //insert new task
    void insertTask(String TaskName, String userID){
        String insertValuesUsers = "INSERT INTO Tasks (idTask,idUser, name)" +
                "values(NULL, '"+userID+"', '"+TaskName+"')";
        database.execSQL(insertValuesUsers);
    }
    //return all tasks for the userID(taskID,TaskName)
    ArrayList<String[]> getTasksByUserId(String id){
        ArrayList <String[]> tasks = new ArrayList<String[]>();
        Cursor cr = database.rawQuery("SELECT * FROM Tasks " +
               "WHERE idUser ='"+id+"'", null);
        if (cr.getCount() > 0)
        {
            cr.moveToFirst();
            do {
                String taskId = cr.getString(cr.getColumnIndex("idTask"));
                String taskName = cr.getString(cr.getColumnIndex("name"));
                String userId = cr.getString(cr.getColumnIndex("idUser"));
                tasks.add(new String[]{taskId,taskName,userId} );
            } while (cr.moveToNext());

        }
        cr.close();
        return tasks;
    }
    // insert new user
    void insertLocaluser(String username, String passwd){
        String id;
        do {id = generateID();} while(ExistId(id));//dokler ni unikaten ID
        insertUserID(id);
        String insertValuesLocalUsers = "INSERT INTO LocalUsers " +
                "(username, password, idUser)"+
                "values('"+username+"','"+passwd+"','"+id+"');";
        database.execSQL(insertValuesLocalUsers);
    }
    //return user ID
    String getLocalUserID(String username){
        String id="";
        Cursor cr= database.rawQuery("SELECT id FROM Users, LocalUsers " +
                "WHERE LocalUsers.idUser == Users.id " +
                "AND LocalUsers.username = '"+username+"'",null);
        cr.moveToFirst();
        id = cr.getString(cr.getColumnIndexOrThrow("id"));
        return id;
    }
    //add time and date (time in seconds, date(yyy-MM-dd))
    void AddTime(String userId,String taskId, int time, String date ){
        String insertValuesUsers = "INSERT INTO Time (idTime,idTask, idUser,time, date)" +
                "values(NULL, '"+taskId+"', '"+userId+"', '"+time+"', '"+date+"')";
        database.execSQL(insertValuesUsers);

    }
    //return array(days, time)
    String[] getTime(String idTask, String idUser){
        int days = 0, time = 0;
        Cursor cr = database.rawQuery("SELECT  DISTINCT date FROM Time " +
                "WHERE idUser ='"+idUser+"' AND " +
                "idTask = '"+idTask+"'", null);
        // if getCount ==0 user does not work on this task return 0 days 0 hours
        if(cr.getCount()<1){
            cr.close();
            return new String[]{String.valueOf(days), String.valueOf(time)};
        }
        else {
            days = cr.getCount();
            cr = database.rawQuery("SELECT  * FROM Time " +
                    "WHERE idUser ='"+idUser+"' AND " +
                    "idTask = '"+idTask+"'", null);
            while (cr.moveToNext())//time in seconds
                time+= Integer.parseInt( cr.getString(cr.getColumnIndex("time")) );
        }

        cr.close();
        return new String[]{String.valueOf(days), String.valueOf(time/3600)};
    }
    void addTaskDescription(int idTask, String idUser, String description){
        String insert = "INSERT INTO TaskDescription (idTaskDescription,idTask, idUser,description)" +
                "values(NULL, "+idTask+", '"+idUser+"', '"+description+"')";
        database.execSQL(insert);
    }
    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        //taski za vsakega userja
        private String createTasks = "CREATE TABLE Tasks"
                + "(idTask INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idUser TEXT NOT NULL ,"
                + "name TEXT NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        // lokalni uporabniki
        private String createLocalUsers = "CREATE TABLE LocalUsers(username TEXT primary key NOT NULL,"
                + "password TEXT NOT NULL,"
                + "idUser INTEGER NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        //lokalni uporabniki + tisti ki se registrirajo z google računom
        private String createUsers = "CREATE TABLE Users"
                + "(id TEXT PRIMARY KEY NOT NULL);";

        // časi za vsako opravilo
        private String createTime = "CREATE TABLE Time"
                + "(idTime INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTask INTEGER NOT NULL,"
                + "idUser TEXT NOT NULL,"
                + "time INTEGER NOT NULL,"
                + "date DATE NOT NULL,"
                + "FOREIGN KEY(idTask) REFERENCES Tasks(idTask)"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        //  opis kaj si naredil v določenem času
        private String createTaskDescription = "CREATE TABLE TaskDescription"
                + "(idTaskDescription INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTask INTEGER NOT NULL,"
                + "idUser TEXT NOT NULL,"
                + "description TEXT NOT NULL,"
                + "FOREIGN KEY(idTask) REFERENCES Tasks(idTask)"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";

        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        // create database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createLocalUsers);
            db.execSQL(createUsers);
            db.execSQL(createTasks);
            db.execSQL(createTime);
            db.execSQL(createTaskDescription);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS Users");
            db.execSQL("DROP TABLE IF EXISTS LocalUsers");
            db.execSQL("DROP TABLE IF EXISTS Tasks");
            db.execSQL("DROP TABLE IF EXISTS Time");
            db.execSQL("DROP TABLE IF EXISTS TaskDescription");
            onCreate(db);
        }
    }
}// end databaseConector
