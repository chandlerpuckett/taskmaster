package com.chandlerpuckett.taskmaster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class AddTask extends AppCompatActivity implements TaskViewAdapter.OnInteractWithTaskListener {
    private Button addTaskBtn;
    private TaskItem newTask;
    ArrayList<Team> teams = new ArrayList<>();
    String lastFileUploadedKey;

//    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
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

        findViewById(R.id.addFileBtn).setOnClickListener(view -> {
            retrieveFile();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult (int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (req == 400){
            Log.i("Amplify pick --- ", "got image back");
            System.out.println(data);

            File fileCopy = new File(getFilesDir(), "pic");

            try {
                InputStream inStream = getContentResolver().openInputStream(data.getData());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("Amplify pic --- ", e.toString());
            }
            uploadFile(fileCopy, fileCopy.getName() + Math.random());
            Log.i("Amplify pic ---", "----- Picture Uploaded ! -----");
        } else {
            Log.i("Amplify pick --- ", "--- FAILURE ---");
        }
    }

    private void retrieveFile(){
        Intent getPic = new Intent(Intent.ACTION_GET_CONTENT);
        getPic.setType("*/*");

//        getPic.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{".png", ".jpg"});

        getPic.addCategory(Intent.CATEGORY_OPENABLE);
        getPic.putExtra(Intent.EXTRA_LOCAL_ONLY, true);


        startActivityForResult(getPic,400);
    }

    private void downloadFile(String fileKey){
        Amplify.Storage.downloadFile(
                fileKey,
                new File(getApplicationContext().getFilesDir() + "/" + fileKey + ".txt"),
                result -> Log.i("Taskmaster", "Successfully Downloaded " + result.getFile().getName()),
                error -> Log.e("Taskmaster", "download fail", error)
        );
    }

    private void uploadFile(File file, String key) {
        lastFileUploadedKey = key;

        Amplify.Storage.uploadFile(
                key,
                file,
                result -> {
                    Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                    downloadFile(key);
                },
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }

    public void addTeamsToList(){
        Team team1 = new Team("1","team1");
        Team team2 = new Team("2","team2");
        Team team3 = new Team("3","team3");

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
    }

    public static void copy (File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0){
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    @Override
    public void taskListener(TaskItem task) {

    }
}