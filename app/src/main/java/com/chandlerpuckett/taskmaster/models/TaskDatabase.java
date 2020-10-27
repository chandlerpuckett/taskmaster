package com.chandlerpuckett.taskmaster.models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.chandlerpuckett.taskmaster.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
