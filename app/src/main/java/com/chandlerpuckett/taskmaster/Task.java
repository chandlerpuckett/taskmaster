package com.chandlerpuckett.taskmaster;

import androidx.constraintlayout.solver.state.State;

public class Task {
    String title, body, state;

    public Task(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }
}
