package koki_kambic.emp_project;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;


public class DatabaseConnector {

    private static final String DATABASE_NAME = "Data";
    private static final int DATABASE_VERSION = 1;
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
    boolean ExistId(int id){
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
    //if password or/and username are incorrect then cr == null(return false)
    boolean CheckPassword(String username, String password){
        Cursor cr = database.rawQuery("SELECT * FROM LocalUsers " +
                "WHERE username = '"+username+"' " +
                "AND password = '"+password+"';",null);
        int i =cr.getCount();
        cr.close();
        return (i != 0);
    }
    //generate id for local users
    int generateID(){
        int min = 10000000;
        int max = 999999999;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    void insertUserID(int id){
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
    //return all tasks for the userID
    ArrayList<String> getTasksByUserId(String id){
        ArrayList <String> tasks = new ArrayList<String>();
        Cursor cr = database.rawQuery("SELECT name FROM Tasks, Users " +
                "WHERE Tasks.idUser = Users.id; " +
                "AND Users.id = '"+id+"'",null);
        if (cr.getCount() > 0)
        {
            cr.moveToFirst();
            do {
                tasks.add( cr.getString(cr.getColumnIndex("name")) );
            } while (cr.moveToNext());
            cr.close();
        }
            return tasks;
    }
    // insert new user
    void insertLocaluser(String username, String passwd){
        int id;
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
    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        //taski za vsakega userja
        private String createTasks = "CREATE TABLE Tasks"
                + "(idTask INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idUser INTEGER NOT NULL ,"
                + "name TEXT NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        // lokalni uporabniki
        private String createLocalUsers = "CREATE TABLE LocalUsers(username TEXT primary key NOT NULL,"
                + "password TEXT NOT NULL,"
                + "idUser INTEGER NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        //lokalni uporabniki + tisti ki se registrirajo z google računom
        private String createUsers = "CREATE TABLE Users"
                + "(id INTEGER PRIMARY KEY NOT NULL);";

        // časi za vsako opravilo
        private String createTime = "CREATE TABLE Time"
                + "(idTime INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTask INTEGER NOT NULL,"
                + "idUser INTEGER NOT NULL,"
                + "Start time NOT NULL,"
                + "stop time NOT NULL,"
                + "date DATE NOT NULL,"
                + "FOREIGN KEY(idTask) REFERENCES Tasks(idTask)"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        //  opis kaj si naredil v določenem času
        private String createTaskDescription = "CREATE TABLE TaskDescription"
                + "(idTaskDescription INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTask INTEGER NOT NULL,"
                + "idUser INTEGER NOT NULL,"
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
