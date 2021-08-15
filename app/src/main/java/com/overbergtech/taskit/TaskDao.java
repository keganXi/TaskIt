package com.overbergtech.taskit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    // allowing the insert of the same data multiple times by
    // passing a conflict resolution strategy.
    @Insert(onConflict = OnConflictStrategy.IGNORE) // ignores a new record if the data is exactly
    void insert(Task task);                         // the same.

    @Query("DELETE FROM task_table")
    void deleteAll();

    @Query("SELECT * FROM task_table ORDER BY title ASC")
    LiveData<List<Task>> getAlphabetizedTasks();
}
