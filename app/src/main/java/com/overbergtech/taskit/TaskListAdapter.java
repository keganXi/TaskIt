package com.overbergtech.taskit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;

import static androidx.core.content.ContextCompat.startActivity;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private LayoutInflater mInflator;
    LinkedList<String> mTaskList;
    Cursor c;
    DBAdapter db;
    String title, created, is_notify;
    TextView noTaskText;


    public TaskListAdapter(Context context, LinkedList<String> taskList, TextView noTaskText){
        this.noTaskText = noTaskText;
        mInflator = LayoutInflater.from(context);
        db = new DBAdapter(context);
        this.mTaskList = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Inflate an item view.
        View mItemView = mInflator.inflate(R.layout.task_list_activity, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position){
        // Retrieve the data for that position.
        String mCurrent = mTaskList.get(position);
        db.open();
        c = db.getAllTasks();
        if(c.moveToFirst()){
            do{
                if(c.getString(0).equals(mCurrent)){
                    title = c.getString(1);
                    created = c.getString(5);
                    is_notify = c.getString(6);
                }
            }while(c.moveToNext());
        }
        db.close();

        // Add the data to the view
        if(is_notify.equals("0")){
            holder.notifyTask.setVisibility(View.GONE);
        }
        holder.taskItemView.setText(title);
        holder.dateItemView.setText(created);
    }


    @Override
    public int getItemCount() {
        return mTaskList.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView deleteTask, notifyTask ;
        TextView taskItemView, dateItemView;
        TaskListAdapter mAdapter;
        DBAdapter db;
        Cursor c;
        Intent intent;
        String passData;

        public TaskViewHolder(View itemView, TaskListAdapter adapter){
            super(itemView);
            taskItemView = (TextView) itemView.findViewById(R.id.taskTitleId);
            dateItemView = (TextView) itemView.findViewById(R.id.taskDateId) ;
            notifyTask = (ImageView) itemView.findViewById(R.id.notifyViewId);
            deleteTask = (ImageView) itemView.findViewById(R.id.deleteTaskId);
            deleteTask.setOnClickListener(
                    this::deleteTaskBtn
            );

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            db = new DBAdapter(v.getContext());
            db.open();
            c = db.getAllTasks();
            if(c.moveToFirst()){
                do{
                    if(c.getString(0).equals(mTaskList.get(getAdapterPosition()))){
                        passData = c.getString(0);
                    }
                }while(c.moveToNext());
            }
            db.close();

            intent = new Intent(v.getContext(), EditTaskActivity.class);
            intent.setData(Uri.parse(passData));
            startActivity(v.getContext(), intent, null);
        }

        private void deleteTaskBtn(View v){
            db = new DBAdapter(v.getContext());
            db.open();
            c = db.getAllTasks();
            if(c.moveToFirst()){
                do{
                    if(c.getString(0).equals(mTaskList.get(getAdapterPosition()))){
                        mTaskList.remove(getAdapterPosition());
                        mAdapter.notifyDataSetChanged();
                        db.deleteTask(c.getLong(0));
                        cancelAlarm(v.getContext(), (int) c.getLong(0));
                        Snackbar.make(v, "Deleted: " + c.getString(1), Snackbar.LENGTH_LONG).show();
                    }
                }while(c.moveToNext());
            }

            if(getItemCount() == 0){
                noTaskText.setVisibility(View.VISIBLE);
            }
            db.close();
        }

        public void cancelAlarm(Context context, int taskID){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alertIntent = new Intent(context, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskID, alertIntent, 0);
            alarmManager.cancel(pendingIntent);
        }

    }
}
