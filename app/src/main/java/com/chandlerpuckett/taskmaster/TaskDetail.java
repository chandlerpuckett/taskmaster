package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskState = findViewById(R.id.taskDetailState);
        TextView taskBody = findViewById(R.id.taskDetailBody);
        Intent intent = getIntent();

        taskTitle.setText(intent.getExtras().getString("title"));
        taskState.setText(intent.getExtras().getString("state"));
        taskBody.setText(intent.getExtras().getString("body"));


    }
}