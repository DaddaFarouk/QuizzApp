package com.farouk.quizzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private Button signUpButton, loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }

        Intent intent = getIntent();
        if (intent.getStringExtra("logout")!=null){
            Toast.makeText(MainActivity.this, intent.getStringExtra("logout"), Toast.LENGTH_LONG).show();
        }

        emailText = findViewById(R.id.email);
        passwordText= findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        signUpButton = findViewById(R.id.signupbtn);
        signUpButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent1);
        });

        loginButton = findViewById(R.id.loginbtn);
        loginButton.setOnClickListener(view -> userLogin());
        //askLocationBtn.setOnClickListener(view -> startActivity(new Intent(this, Location.class)));

    }

    private void userLogin(){
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (email.isEmpty()){
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please enter a valid email!");
            emailText.requestFocus();
            return;
        }

        if (password.isEmpty()){
            emailText.setError("Password is required!");
            emailText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        if(user.isEmailVerified()){
                            startActivity(new Intent(MainActivity.this, Quizzes.class));
                        }
                        else{
                            user.sendEmailVerification();
                            Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}