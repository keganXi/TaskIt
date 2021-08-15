package com.overbergtech.taskit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

public class CreateTaskActivity extends AppCompatActivity {

    Intent intent;
    boolean set_notify;
    int getHour, getMinute, getYear, getMonth, getDay, taskID;
    View[] notifyObjects;
    // UI widgets.
    EditText taskTitle, taskDescription, taskDate, taskTime;
    Switch setNotificationSwitch;
    TextView dateText;
    ImageView calenderView, timeView;
    // Database.
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskTitle = (EditText) findViewById(R.id.titleInputId);
        taskDescription = (EditText) findViewById(R.id.descriptionInputId);
        taskDate = (EditText) findViewById(R.id.dateInputId);
        taskTime = (EditText) findViewById(R.id.timeInputId);
        dateText = (TextView) findViewById(R.id.dateTextId);
        calenderView = (ImageView) findViewById(R.id.calendarViewId);
        timeView = (ImageView) findViewById(R.id.timeViewId);
        setNotificationSwitch = (Switch) findViewById(R.id.setNotificationId);

        // Array containing notification widgets.
        notifyObjects = new View[]{
                dateText, taskDate, taskTime, calenderView, timeView
        };
        if(!setNotificationSwitch.isChecked()){
            hideNotificationUI(notifyObjects);
        }
    }


    public void clickSetNotification(View view) {
        if(setNotificationSwitch.isChecked()){
            displayNotificationUI(notifyObjects);
        }else{
            hideNotificationUI(notifyObjects);
        }
    }

    public void displayNotificationUI(View[] notifyObjects){
        for (View notifyObject : notifyObjects) {
            notifyObject.setVisibility(View.VISIBLE);
        }
        set_notify = true;
    }


    public void hideNotificationUI(View[] notifyObjects){
        for (View notifyObject : notifyObjects) {
            notifyObject.setVisibility(View.GONE);
        }
        set_notify = false;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveTask(View view) {
        if(!set_notify){
            taskDate.setText(null);
            taskTime.setText(null);
        }else{
            // set date notification.
            Calendar setDate = Calendar.getInstance();
            setDate.set(Calendar.YEAR, getYear);
            setDate.set(Calendar.MONTH, getMonth);
            setDate.set(Calendar.DAY_OF_MONTH, getDay);
            setDate.set(Calendar.HOUR_OF_DAY, getHour);
            setDate.set(Calendar.MINUTE, getMinute);
            setDate.set(Calendar.SECOND, 0);
            // call startAlarm.
            startAlarm(setDate);
        }

        String getTaskTitle = taskTitle.getText().toString();
        String getTaskDescription = taskDescription.getText().toString();
        String getTaskDate = taskDate.getText().toString();
        String getTaskTime = taskTime.getText().toString();

        // save DB Function.
        saveDB(getTaskTitle, getTaskDescription, getTaskDate, getTaskTime);

        // Start new activity (home).
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void saveDB(String title, String description, String date, String time){
        DateTime dt = new DateTime();
        db = new DBAdapter(this);
        db.open();
        taskID = (int) db.insertTask(title, description, date, time, dt.getDateTime(), set_notify);
        db.close();
    }


    public void showDatePickerDialog(View v){
        DialogFragment getDateFragment = new DatePickerFragment();
        getDateFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public void showTimePickerDialog(View v){
        DialogFragment getTimeFragment = new TimePickerFragment();
        getTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }


    public void processDatePickerResult(int year, int month, int day){
        getYear = year;
        getMonth = month;
        getDay = day;
        // Convert date elements into strings.
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);

        String full_date = (day_string + "/" + month_string + "/" + year_string);
        taskDate.setText(full_date);
    }


    public void processTimePickerResult(int hourOfDay, int minute){
        getHour = hourOfDay;
        getMinute = minute;
        // Convert time elements into strings. 
        String hour_string = Integer.toString(hourOfDay);
        String minute_string = Integer.toString(minute);

        String full_time = (hour_string + ":" + minute_string);
        taskTime.setText(full_time);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startAlarm(Calendar setDate){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra("title", taskTitle.getText().toString());
        alertIntent.putExtra("description", taskDescription.getText().toString());
        alertIntent.putExtra("taskID", taskID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskID, alertIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
    }

}
