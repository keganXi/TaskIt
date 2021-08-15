package com.overbergtech.taskit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotifyTasks {

    NotificationManager notifyManager;
    NotificationCompat.Builder mBuilder;
    Context ctx;

    public NotifyTasks(Context ctx, NotificationManager notifyManager){
        this.notifyManager = notifyManager;
        this.ctx = ctx;
        mBuilder = new NotificationCompat.Builder(ctx);
    }


    public void setNotifyContent(String contentTitle, String contentText){
        mBuilder.setSmallIcon(R.drawable.ic_time);
        mBuilder.setContentTitle(contentTitle);
        mBuilder.setContentText(contentText);
    }


    public void startNotifyActivity(Intent intent){
        PendingIntent pendingContentIntent = PendingIntent.getActivity(
                ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingContentIntent);
    }


    public void sendNotification(){
        notifyManager.notify(0, mBuilder.build());
    }
}
