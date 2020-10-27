package com.chandlerpuckett.taskmaster.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.chandlerpuckett.taskmaster.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    public void saveTask(Task task);

    @Query("SELECT * FROM task")
    public List<Task> getAll();

    @Query("SELECT * FROM task ORDER BY id DESC")
    public List<Task> getAllTasksReversed();



}
