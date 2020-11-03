package com.chandlerpuckett.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

public class SignupConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirmation);

        ((Button) findViewById(R.id.confirmationBtn)).setOnClickListener(view -> {
            String username = ((TextView) findViewById(R.id.confirmUsername)).getText().toString();
            String verificationCode = ((TextView) findViewById(R.id.verificationCodeInput)).getText().toString();

            Amplify.Auth.confirmSignUp(
                    username,
                    verificationCode,
                    result -> {
                        Log.i("Amplify.confirm", result.isSignUpComplete() ? "Confirm Signup Succeeded" :
                                "Confirm SignUp Not Complete");
                        startActivity(new Intent(SignupConfirmationActivity.this, SignInActivity.class));
                    },
                    error -> Log.e("Amplify.confirm", error.toString())
            );
        });

    }
}