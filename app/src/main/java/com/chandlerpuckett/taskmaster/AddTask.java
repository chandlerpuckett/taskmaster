package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.chandlerpuckett.taskmaster.models.Database;
import com.chandlerpuckett.taskmaster.models.Task;
import com.google.android.material.snackbar.Snackbar;

public class AddTask extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskBtn;

    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        configureAws();

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

//                ----- create TaskItem, save to DynamoDB -----
                TaskItem newTask = TaskItem.builder()
                        .title(titleIn).body(bodyIn).state("new")
                        .build();

                Amplify.API.mutate(ModelMutation.create(newTask),
                        response -> Log.i("Amplify", "---- successfully added to DYNAMO -------"),
                        error -> Log.e("Amplify", error.toString()));


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

    @Override
    public void taskListener(Task task) {

    }
}