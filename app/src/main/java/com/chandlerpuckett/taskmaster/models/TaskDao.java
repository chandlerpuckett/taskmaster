package com.chandlerpuckett.taskmaster.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public void saveTask(Task task);

    @Query("SELECT * FROM Task")
    public List<Task> getAllTasks();

    @Query("SELECT * FROM Task ORDER BY id DESC")
    public List<Task> getAllTasksReversed();

}
