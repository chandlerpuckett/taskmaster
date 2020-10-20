package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class AddTask extends AppCompatActivity {
    private Button addTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        final Snackbar snackbar = Snackbar
                .make(findViewById(R.id.myCoordinatorLayout), R.string.success,
                Snackbar.LENGTH_SHORT);

        addTaskBtn = (Button) findViewById(R.id.btn_addTask);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.show();
            }
        });

    }

}