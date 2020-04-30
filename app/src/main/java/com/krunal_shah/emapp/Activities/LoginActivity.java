package com.krunal_shah.emapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krunal_shah.emapp.R;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmailId);
        password = findViewById(R.id.editTextPassword);
        btnSignIn = findViewById(R.id.buttonSignIn);
        tvSignUp = findViewById(R.id.textViewSignUp);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if (mFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You are logged in.", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Login.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailInfo = emailId.getText().toString();
                String passwordInfo = password.getText().toString();

                if (emailInfo.isEmpty()) {
                    emailId.setError("Please enter your Email ID.");
                    emailId.requestFocus();
                } else if (passwordInfo.isEmpty()) {
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else if (emailInfo.isEmpty() && passwordInfo.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailInfo.isEmpty() && passwordInfo.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(emailInfo, passwordInfo).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Email", emailInfo);
                                editor.apply();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
