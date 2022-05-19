package com.farouk.quizzapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Quizzes extends AppCompatActivity {

    public static final String TAG = "SetDataToViews";
    private TextView textView;
    private ImageView imageView;
    private Button option1Btn;
    private Button option2Btn;
    private Button option3Btn;
    private int currentScore = 0, questionAttempted = 1, currentPos=1;
    private String answer;

    private FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        textView = findViewById(R.id.attempted);
        imageView = findViewById(R.id.image);
        option1Btn = findViewById(R.id.option1);
        option2Btn = findViewById(R.id.option2);
        option3Btn = findViewById(R.id.option3);
        Button giveUpBtn = findViewById(R.id.giveUp);

        dbroot = FirebaseFirestore.getInstance();

        SetDataToViews(currentPos);

        option1Btn.setOnClickListener(view -> {
            if(checkAnswer(option1Btn.getText().toString())){
                currentScore++;
            }
            questionAttempted++;
            currentPos++;
            SetDataToViews(currentPos);
        });

        option2Btn.setOnClickListener(view -> {
            if(checkAnswer(option2Btn.getText().toString())){

                currentScore++;
            }
            questionAttempted++;
            currentPos++;
            SetDataToViews(currentPos);
        });

        option3Btn.setOnClickListener(view -> {
            if(checkAnswer(option3Btn.getText().toString())){

                currentScore++;
            }
            questionAttempted++;
            currentPos++;
            SetDataToViews(currentPos);
        });

        giveUpBtn.setOnClickListener(view -> showScore());


    }

     private boolean checkAnswer(String answer) {
        return answer.trim().equals(this.answer.trim());
    }

     private void SetDataToViews(int val){
        if (questionAttempted > 10){
            showScore();
        }
        else {
            String attempts = "Question : "+questionAttempted+"/10" ;
            textView.setText(attempts);
            dbroot.collection("Questions").document(String.valueOf(val))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                answer = document.getString("answer");
                                Picasso.get().load(document.getString("url")).into(imageView);
                                Toast.makeText(getApplicationContext(), "Loading Image...",Toast.LENGTH_SHORT).show();
                                option1Btn.setText(document.getString("option_1"));
                                option2Btn.setText((document.getString("option_2")));
                                option3Btn.setText((document.getString("option_3")));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    });
        }
    }

    private void showScore(){
        Intent intent = new Intent( Quizzes.this, Score.class);
        intent.putExtra("currentScore",currentScore);
        startActivity(intent);
    }
}
