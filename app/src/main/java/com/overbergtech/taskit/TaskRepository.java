package com.overbergtech.taskit;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    // Note that in order to unit test the TaskRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    TaskRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAlphabetizedTasks();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Task task){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mTaskDao.insert(task);
            }
        });
    }

}
