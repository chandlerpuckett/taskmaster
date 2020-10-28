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

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            TaskItem task = TaskItem.builder()
                    .title("Grocery's").body("go to QFC").state("new")
                    .build();

            Amplify.API.mutate(ModelMutation.create(task),
                    response -> Log.i("Amplify", "successfully added"),
                    error -> Log.e("Amplify", error.toString()));

        } catch (AmplifyException e) {
            e.printStackTrace();
        }


        database = Room.databaseBuilder(getApplicationContext(), Database.class, "puckett_task_database")
                .allowMainThreadQueries()
                .build();

        addTaskBtn = (Button) findViewById(R.id.btn_addTask);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ---- save to DB ----
                EditText titleInput = AddTask.this.findViewById(R.id.titleInput);
                String titleIn = titleInput.getText().toString();

                System.out.println(titleIn);

                EditText bodyInput = AddTask.this.findViewById(R.id.bodyIn);
                String bodyIn = bodyInput.getText().toString();

                System.out.println(bodyIn);

                Task task = new Task(titleIn, bodyIn, "new");
                database.taskDao().saveTask(task);


                Toast toast = Toast.makeText(AddTask.this, "Task Saved!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    @Override
    public void taskListener(Task task) {

    }
}