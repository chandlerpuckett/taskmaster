package com.chandlerpuckett.taskmaster;

import androidx.annotation.NonNull;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.chandlerpuckett.taskmaster.models.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskButton;
    private Button allTasksButton;

    ArrayList<TaskItem> taskItems;
    ArrayList<Team> teams;
    RecyclerView recyclerView;

//    Database database;

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userDisplay = findViewById(R.id.usernameDisplay);
        String user = String.format("%s's tasks", pref.getString("username", "Enter a username"));
        userDisplay.setText(user);

        Handler handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

        connectToRecyclerView(handler);



//        ------ OLD LOCAL DB RECYCLER VIEW ---------

//        database = Room.databaseBuilder(getApplicationContext(), Database.class, "puckett_task_database")
//                .allowMainThreadQueries()
//                .build();


//        ---------- RECYCLER VIEW --------------

//        ArrayList<Task> tasks = (ArrayList<Task>) database.taskDao().getAllTasksReversed();
//
//        RecyclerView recView = findViewById(R.id.homeRecyclerView);
//        recView.setLayoutManager(new LinearLayoutManager(this));
//        recView.setAdapter(new TaskViewAdapter(tasks, this));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureAws();
        teamCreation();


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

//    ----- AWS Helper Methods ------
    private void configureAws(){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

        } catch (AmplifyException e) {
            e.printStackTrace();
        }
    }

    private void connectToRecyclerView(Handler handler){
        taskItems = new ArrayList<>();
        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskViewAdapter(taskItems, this));

        Amplify.API.query(
                ModelQuery.list(TaskItem.class),
                response -> {
                    for (TaskItem task : response.getData()){
                        taskItems.add(task);
                    }
                    handler.sendEmptyMessage(1);
                    Log.i("Amplify query Items", "we pulled this many items from Dynamo " + taskItems.size());
                },
                error -> Log.i("Amplify.queryItems", "Did not get items"));

    }

    private void teamCreation(){
        Team team1 = Team.builder()
                .name("Team1")
                .build();

        Team team2 = Team.builder()
                .name("Team2")
                .build();

        Team team3 = Team.builder()
                .name("Team3")
                .build();

        Amplify.API.mutate(ModelMutation.create(team1),
                response -> Log.i("Amplify", "---- Added team1 ----"),
                error -> Log.e("Amplify","---- failed to add team1 ----"));

        Amplify.API.mutate(ModelMutation.create(team2),
                response -> Log.i("Amplify", "----- added team2 -----"),
                error -> Log.e("amplify", "---- failed to add team2 ----"));

        Amplify.API.mutate(ModelMutation.create(team3),
                response -> Log.i("Amplify", "----- added team3 -----"),
                error -> Log.e("amplify", "---- failed to add team3 ----"));
    }

    @Override
    public void taskListener(TaskItem task) {
        Intent intent = new Intent(MainActivity.this, TaskDetail.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("state", task.getState());
        intent.putExtra("body", task.getBody());
        startActivity(intent);
    }
}