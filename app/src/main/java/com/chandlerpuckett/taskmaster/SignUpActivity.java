package com.chandlerpuckett.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ((Button) findViewById(R.id.userSignUpBtn)).setOnClickListener(view ->{
            String username = ((TextView) findViewById(R.id.signUpUsername)).getText().toString();
            String password = ((TextView) findViewById(R.id.userSignUpPassword)).getText().toString();
            String email = ((TextView) findViewById(R.id.userEmailSignUp)).getText().toString();

            Amplify.Auth.signUp(
                    username,
                    password,
                    AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(),email).build(),
                    result -> {
                        Log.i("Amplify.signup", "Result: " + result.toString());
                        startActivity(new Intent(SignUpActivity.this, SignupConfirmationActivity.class));
                    },
                    error -> Log.e("Amplify.signup", "Sign up failed", error)
            );
        });
    }
}