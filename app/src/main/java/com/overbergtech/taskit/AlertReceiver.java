package com.overbergtech.taskit;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AlertReceiver extends BroadcastReceiver {

    Intent taskIntent;
    String taskTitle, taskDescription, taskID;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Task ID
        taskID = Integer.toString(intent.getIntExtra("taskID", 0));
        // get edit task activity intent.
        // set notification manager for NotifyTasks.
        taskIntent = new Intent(context, EditTaskActivity.class);
        taskIntent.setData(Uri.parse(taskID));
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // get intent extras (task title and task description).
        taskTitle = intent.getStringExtra("title");
        taskDescription = intent.getStringExtra("description");
        // call and set task notification.
        NotifyTasks notifyTasks = new NotifyTasks(context, notifyManager);
        notifyTasks.setNotifyContent(taskTitle, taskDescription);
        notifyTasks.startNotifyActivity(taskIntent);
        notifyTasks.sendNotification();
    }
}
