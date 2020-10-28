package com.chandlerpuckett.taskmaster.models;

import androidx.room.RoomDatabase;
import androidx.room.*;

@androidx.room.Database(entities = {Task.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract TaskDao taskDao();
}
