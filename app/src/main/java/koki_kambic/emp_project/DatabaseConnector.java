package koki_kambic.emp_project;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseConnector {

    public static final String DATABASE_NAME = "Data";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        //create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) database.close();
    }

    public boolean UserExist(String username){
        Cursor cr =  null;
        cr =database.rawQuery("SELECT * FROM LocalUsers " +
                "WHERE username = 'nejc';",null);
        return (cr == null) ? false:true;
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        // lokalni uporabniki
        private String createLocalUsers = "CREATE TABLE LocalUsers(username TEXT primary key NOT NULL,"
                + "password TEXT NOT NULL,"
                + "idUser INTEGER NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
        //lokalni uporabniki + tisti ki se registrirajo z google računom
        private String createUsers = "CREATE TABLE Users"
                + "(id INTEGER PRIMARY KEY NOT NULL);";
        //taski za vsakega userja
        private String createTasks = "CREATE TABLE Tasks"
                + "(idTask INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idUser INTEGER NOT NULL ,"
                + "name TEXT NOT NULL,"
                + "FOREIGN KEY(idUser) REFERENCES Users(id));";
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
