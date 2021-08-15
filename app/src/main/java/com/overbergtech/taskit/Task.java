package com.overbergtech.taskit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table") // database table.
public class Task {

    @PrimaryKey
    @NonNull // a parameter can never be a null value.
    @ColumnInfo(name = "title") // specifies the name of the column.
    private String mTitle;

    @ColumnInfo(name = "description")
    @NonNull
    private String mDescription;

    public Task(@NonNull String title, @NonNull String description){
        this.mTitle = title;
        this.mDescription = description;
    }

    public String getTitle(){
        return this.mTitle;
    }

    public String getDescription(){
        return this.mDescription;
    }

}
