package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;

import com.amplifyframework.datastore.generated.model.Team;
import com.chandlerpuckett.taskmaster.models.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class AddTask extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskBtn;
    private TaskItem newTask;
    ArrayList<Team> teams = new ArrayList<>();


//    Database database;

    // save each teams into database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        configureAws();
        addTeamsToList();

        System.out.println(teams.toString());

//        ----- old DB config -----
//        database = Room.databaseBuilder(getApplicationContext(), Database.class, "puckett_task_database")
//                .allowMainThreadQueries()
//                .build();

        addTaskBtn = (Button) findViewById(R.id.btn_addTask);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

//                ---- get text from inputs ----
                EditText titleInput = AddTask.this.findViewById(R.id.titleInput);
                String titleIn = titleInput.getText().toString();

                EditText bodyInput = AddTask.this.findViewById(R.id.bodyIn);
                String bodyIn = bodyInput.getText().toString();

//                ----- radio button select -----
                RadioGroup group = findViewById(R.id.radioBtnGroup);
                RadioButton btn = findViewById(group.getCheckedRadioButtonId());
                int teamIdx = parseInt(btn.getContentDescription().toString());
                System.out.println("selected button IDX ------- " + teamIdx);

//                ----- create TaskItem, save to DynamoDB -----
                newTask = TaskItem.builder()
                        .title(titleIn).body(bodyIn).state("new").foundAt(teams.get(teamIdx))
                        .build();

                Amplify.API.mutate(ModelMutation.create(newTask),
                        response -> Log.i("Amplify", "---- successfully added to DYNAMO -------"),
                        error -> Log.e("Amplify", error.toString()));

                System.out.println(newTask);

//                ----- save to local DB -----
//                Task task = new Task(titleIn, bodyIn, "new");
//                database.taskDao().saveTask(task);

                Toast toast = Toast.makeText(AddTask.this, "Task Saved!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    private void configureAws(){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

        } catch (AmplifyException e) {
            e.printStackTrace();
        }
    }

    private void addTeamsToList(){
        Team team1 = new Team("1","team1");
        Team team2 = new Team("2","team2");
        Team team3 = new Team("3","team3");

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
    }

    @Override
    public void taskListener(TaskItem task) {

    }
}