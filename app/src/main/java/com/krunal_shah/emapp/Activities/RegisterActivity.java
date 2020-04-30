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
import com.krunal_shah.emapp.R;

public class RegisterActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmailId);
        password = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvSignIn = findViewById(R.id.textViewSignIn);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("Email", "");

        if (user.isEmpty()) {

            btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(RegisterActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    } else if (!(emailInfo.isEmpty() && passwordInfo.isEmpty())) {
                        mFirebaseAuth.createUserWithEmailAndPassword(emailInfo, passwordInfo).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Sign Up Failed, Please try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("Email", emailInfo);
                                    editor.apply();

                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        } else {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
    }
}
