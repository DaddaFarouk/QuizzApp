package com.farouk.quizzapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private EditText fullnameText, emailText, usernameText, passwordText;
    private Button submitBtn;
    private Intent intent;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();


        fullnameText = findViewById(R.id.fullname);
        emailText = findViewById(R.id.email);
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        submitBtn = findViewById(R.id.submitbtn);

        submitBtn.setOnClickListener(view -> {
            registerUser();
        } );

    }

    private void registerUser() {
        String fullname = fullnameText.getText().toString();
        String email = emailText.getText().toString().trim();
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();

        if (fullname.isEmpty()){
            fullnameText.setError("Full Name is required!");
            fullnameText.requestFocus();
            return;
        }
        if (email.isEmpty()){
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email is not valid!");
            emailText.requestFocus();
            return;
        }
        if (username.isEmpty()){
            usernameText.setError("Username is required!");
            usernameText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordText.setError("Password is required!");
            passwordText.requestFocus();
            return;
        }
        if (password.length()<6){
            passwordText.setError("Min password length should be 6 characters!");
            passwordText.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            User user = new User(fullname, username, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUp.this,"User has been registered successfully!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(SignUp.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        else{
                            Toast.makeText(SignUp.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        startActivity(new Intent(SignUp.this,MainActivity.class).putExtra("success","User has been registered successfully!"));

    }

}