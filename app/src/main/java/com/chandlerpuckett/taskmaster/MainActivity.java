package com.chandlerpuckett.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.chandlerpuckett.taskmaster.models.Database;
import com.chandlerpuckett.taskmaster.models.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskButton;
    private Button allTasksButton;

    Database database;

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userDisplay = findViewById(R.id.usernameDisplay);
        String user = String.format("%s's tasks", pref.getString("username", "Enter a username"));
        userDisplay.setText(user);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "puckett_task_database")
                .allowMainThreadQueries()
                .build();


        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());


        } catch (AmplifyException e) {
            e.printStackTrace();
        }


//        ---------- RECYCLER VIEW --------------

        ArrayList<Task> tasks = (ArrayList<Task>) database.taskDao().getAllTasksReversed();

        RecyclerView recView = findViewById(R.id.homeRecyclerView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(new TaskViewAdapter(tasks, this));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        ---- task & settings buttons ----
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

    public void openTaskDetailPage(View view){
        Intent intent = new Intent(this, TaskDetail.class);
        Button task = (Button) view;
        String btnTxt = task.getText().toString();

        System.out.println("---- TESTING TESTING ----");
        System.out.println(btnTxt);

        intent.putExtra("task",btnTxt);
        startActivity(intent);
    }

    @Override
    public void taskListener(Task task) {
        Intent intent = new Intent(MainActivity.this, TaskDetail.class);
        intent.putExtra("title", task.title);
        intent.putExtra("state", task.state);
        intent.putExtra("body", task.body);
        startActivity(intent);
    }
}