package com.chandlerpuckett.taskmaster;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
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
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.chandlerpuckett.taskmaster.models.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskButton;
    private Button allTasksButton;

    ArrayList<TaskItem> taskItems;
    ArrayList<Team> teams;
    RecyclerView recyclerView;

    public static final String TAG = "Amplify";

    private static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        final String token = task.getResult().getToken();
                        Log.d(TAG, "Registering push notifications token: " + token);
                        pinpointManager.getNotificationClient().registerDeviceToken(token);
                    });
        }
        return pinpointManager;
    }

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
        uploadFile();
        getPinpointManager(getApplicationContext());

//      TODO: query Dynamo, only create teams if Table is empty
//        teamCreation();


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

        findViewById(R.id.goToSignUpBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { openSignUpPage(); }
        });

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openLoginPage(); }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { logout(); }
        });

    }

//    ----- BUTTON ROUTES -----
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

    public void openSignUpPage(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void openLoginPage(){
        Intent intent = new Intent(this, SignInActivity.class);
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


//    --- Cognito Logout ---
    public void logout(){
        // TODO: move toast to helper method -> sort out Looper error
        Toast toast = Toast.makeText(this, "Logout Successful", Toast.LENGTH_LONG);
        toast.show();

        Amplify.Auth.signOut(
                () -> logoutHelper(),
                error -> Log.e("Amplify.signOut", error.toString())
        );
    }

    private void logoutHelper(){
        // show toast


        // wipes username
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().remove("username").apply();

        // reloads page
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

//    ----- AWS Helper Methods ------
    private void configureAws(){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
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

//  --- S3 BUCKET ---
    private void uploadFile() {
        File exampleFile = new File(getApplicationContext().getFilesDir(), "MyBucketTest");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("Amplify.s3", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                "MyBucketTest",
                exampleFile,
                result -> Log.i("Amplify.s3", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("Amplify.s3", "Upload failed", storageFailure)
        );
    }

    public void retrieveFile(){
        Intent getPicIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getPicIntent.setType("*/*");
        getPicIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{".png",".jpg"});
        getPicIntent.addCategory(Intent.CATEGORY_OPENABLE);
    }

}