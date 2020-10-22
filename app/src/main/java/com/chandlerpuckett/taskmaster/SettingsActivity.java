package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor prefEdit = preferences.edit();

        findViewById(R.id.saveUserBtn).setOnClickListener((view) -> {
            EditText user = findViewById(R.id.enterUsername);
            prefEdit.putString("username", user.getText().toString());
            prefEdit.apply();

            Toast toast = Toast.makeText(this, "Username Save Successful", Toast.LENGTH_LONG);
            toast.show();
        });
    }
}