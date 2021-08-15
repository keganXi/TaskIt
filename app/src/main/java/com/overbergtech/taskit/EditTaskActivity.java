package com.overbergtech.taskit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    Intent intent;
    EditText editTitle, editDescription, editDate, editTime;
    TextView dateText;
    ImageView calenderView, timeView;
    Switch setNotificationSwitch;
    Cursor c;
    DBAdapter db;
    String getTitle, getDescription, getDate, getTime, getCreated,
            getIsNotify, saveTitle, saveDescription, saveDate, saveTime;
    boolean set_notify;
    long rowId;
    int getHour, getMinute, getYear, getMonth, getDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        getEditTaskData();

        editTitle = (EditText) findViewById(R.id.editTitleId);
        editDescription = (EditText) findViewById(R.id.editDescriptionId);
        editDate = (EditText) findViewById(R.id.editDateInputId);
        editTime = (EditText) findViewById(R.id.editTimeInputId);
        setNotificationSwitch = (Switch) findViewById(R.id.setNotificationId);
        dateText = (TextView) findViewById(R.id.dateTextId);
        calenderView = (ImageView) findViewById(R.id.calendarViewId);
        timeView = (ImageView) findViewById(R.id.timeViewId);

        editTitle.setText(getTitle);
        editDescription.setText(getDescription);
        editDate.setText(getDate);
        editTime.setText(getTime);
        setNotificationSwitch.setChecked(!getIsNotify.equals("0"));

        if(!setNotificationSwitch.isChecked()){
            hideNotificationUI();
        }
    }


    public void editSetNotification(View view) {
        if(setNotificationSwitch.isChecked()){
            dateText.setVisibility(View.VISIBLE);
            editDate.setVisibility(View.VISIBLE);
            editTime.setVisibility(View.VISIBLE);
            calenderView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
            set_notify = true;
        }else{
            hideNotificationUI();
        }
    }


    public void getEditTaskData(){
        intent = getIntent();
        db = new DBAdapter(this);
        db.open();
        c = db.getAllTasks();
        if(c.moveToFirst()){
            do{
                if(c.getString(0).equals(intent.getDataString())){
                    getTitle = c.getString(1);
                    getDescription = c.getString(2);
                    getDate = c.getString(3);
                    getTime = c.getString(4);
                    getCreated = c.getString(5);
                    getIsNotify = c.getString(6);
                    rowId = c.getLong(0);
                }
            }while(c.moveToNext());
        }
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveEditTask(View view) {
        if(!set_notify){
            editDate.setText(null);
            editTime.setText(null);
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
        saveTitle = editTitle.getText().toString();
        saveDescription = editDescription.getText().toString();
        saveDate = editDate.getText().toString();
        saveTime = editTime.getText().toString();
        db = new DBAdapter(this);
        db.open();
        db.updateTask(rowId, saveTitle, saveDescription, saveDate, saveTime, getCreated, set_notify);
        db.close();

        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Edited: " + saveTitle, Toast.LENGTH_LONG).show();
    }


    public void editCalendarClick(View view) {
        showDatePickerDialog(view);
    }


    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new EditDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
        editDate.setText(full_date);
    }


    public void showTimePickerDialog(View view) {
        DialogFragment getTimeFragment = new EditTimePickerFragment();
        getTimeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void processTimePickerResult(int hourOfDay, int minute){
        getHour = hourOfDay;
        getMinute= minute;
        // Convert time elements into strings.
        String hour_string = Integer.toString(hourOfDay);
        String minute_string = Integer.toString(minute);

        String full_time = (hour_string + ":" + minute_string);
        editTime.setText(full_time);
    }


    public void hideNotificationUI(){
        dateText.setVisibility(View.GONE);
        editDate.setVisibility(View.GONE);
        editTime.setVisibility(View.GONE);
        calenderView.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);
        set_notify = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startAlarm(Calendar setDate){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra("title", saveTitle);
        alertIntent.putExtra("description", saveDescription);
        alertIntent.putExtra("taskID", rowId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) rowId, alertIntent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, setDate.getTimeInMillis(), pendingIntent);
    }

}
