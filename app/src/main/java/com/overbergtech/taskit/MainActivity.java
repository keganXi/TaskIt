package com.overbergtech.taskit;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    TaskListAdapter mAdapter;
    LinkedList<String> mTaskList, getTask;
    DBAdapter db;
    TextView noTaskText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noTaskText = (TextView) findViewById(R.id.noTaskText);
        noTaskText.setVisibility(View.VISIBLE);

        mTaskList = getTaskList();
        if (!(mTaskList.size() == 0)){
            noTaskText.setVisibility(View.GONE);
            mRecyclerView = (RecyclerView) findViewById(R.id.taskRecyclerId);
            mAdapter = new TaskListAdapter(this, mTaskList, noTaskText);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public LinkedList<String> getTaskList(){
        db = new DBAdapter(this);
        //---add a task---
        db.open();
        getTask = new LinkedList<String>();
        Cursor c = db.getAllTasks();
        if(c.moveToFirst()){
            do{
                getTask.add(c.getString(0));
            }while(c.moveToNext());
        }
        db.close();
        return getTask;
    }

    public void addTaskBtn(View view) {
        Intent createTaskIntent = new Intent(this, CreateTaskActivity.class);
        startActivity(createTaskIntent);
    }


}
