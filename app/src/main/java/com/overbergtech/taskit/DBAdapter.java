package com.overbergtech.taskit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    static final String KEY_ROWID = "_id";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_DATE = "date";
    static final String KEY_TIME = "time";
    static final String KEY_CREATED = "created";
    static final String KEY_NOTIFICATION = "notify";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "TaskDB";
    static final String DATABASE_TABLE = "tasks";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE =
            "create table tasks (_id integer primary key autoincrement, " +
                    "title text not null, description text not null, date text, time text," +
                    " created text not null, notify boolean);";
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            try{
                db.execSQL(DATABASE_CREATE);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS tasks");
            onCreate(db);
        }

    }


    //---opens the database---
    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close(){
        DBHelper.close();
    }

    //---insert a task into the database---
    public long insertTask(String title, String description, String date,
                           String time, String created, boolean notify){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_CREATED, created);
        initialValues.put(KEY_NOTIFICATION, notify);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---delete a particular task---
    public boolean deleteTask(long rowId){
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the tasks---
    public Cursor getAllTasks(){
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_DESCRIPTION,
                        KEY_DATE, KEY_TIME, KEY_CREATED, KEY_NOTIFICATION},
                null, null, null, null, null);
    }

    //---retrieves a particular task---
    public Cursor getTask(long rowId) throws SQLException{
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                        KEY_DESCRIPTION, KEY_DATE, KEY_TIME, KEY_CREATED, KEY_NOTIFICATION},
                KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a task---
    public boolean updateTask(long rowId, String title, String description, String date,
                              String time, String created, boolean notify){
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DESCRIPTION, description);
        args.put(KEY_DATE, date);
        args.put(KEY_TIME, time);
        args.put(KEY_CREATED, created);
        args.put(KEY_NOTIFICATION, notify);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


}
