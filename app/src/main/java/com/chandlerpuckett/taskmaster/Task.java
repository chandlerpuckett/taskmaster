package com.chandlerpuckett.taskmaster;

import androidx.constraintlayout.solver.state.State;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    public long id;

    String title, body, state;

    public Task(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }
}
