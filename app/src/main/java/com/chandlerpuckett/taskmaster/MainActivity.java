package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button addTaskButton;
    private Button allTasksButton;

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userDisplay = findViewById(R.id.usernameDisplay);
        String user = String.format("%s's tasks", pref.getString("username", "Enter a username"));
        userDisplay.setText(user);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTaskButton = (Button) findViewById(R.id.addTask);
        addTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openAddTaskPage();
            }
        });

        allTasksButton = (Button) findViewById(R.id.allTasks);
        allTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllTasksPage();
            }
        });

        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingPage();
            }
        });

    }

    public void openAddTaskPage(){
        Intent intent = new Intent(this, AddTask.class);
        startActivity(intent);
    }

    public void openAllTasksPage(){
        Intent intent = new Intent(this, AllTasks.class);
        startActivity(intent);
    }

    public void openSettingPage(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

}