package com.chandlerpuckett.taskmaster.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.amplifyframework.datastore.generated.model.TaskItem;

@Entity
public class File {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public TaskItem taskItem;
    
}
