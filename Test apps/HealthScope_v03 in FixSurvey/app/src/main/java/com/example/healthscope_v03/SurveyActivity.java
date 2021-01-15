package com.example.healthscope_v03;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthscope_v03.CustomViews.CheckboxQuestion;

public class SurveyActivity extends AppCompatActivity {

    CheckboxQuestion q1, q2, q3;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        q1 = findViewById(R.id.question1);
        q2 = findViewById(R.id.question2);
        q3 = findViewById(R.id.question3);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q1.getAnswer() == 3 || q2.getAnswer() == 3 || q3.getAnswer() == 3) {
                    Toast.makeText(SurveyActivity.this, "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SurveyActivity.this, "Survey Completed.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SurveyActivity.this, MainActivity.class);
                int finalAnswers = 0;
                if (q1.getAnswer() == 2){
                    finalAnswers = 200;
                } else {
                    finalAnswers = 100;
                }
                if (q2.getAnswer() == 2){
                    finalAnswers = finalAnswers + 20;
                } else{
                    finalAnswers = finalAnswers + 10;
                }
                if (q3.getAnswer() == 2){
                    finalAnswers = finalAnswers + 2;
                } else {
                    finalAnswers = finalAnswers + 1;
                }

                Log.d("Survey Results", "onClick: " + finalAnswers);
                i.putExtra("Survey Results", finalAnswers);
                startActivity(i);
            }
        });
    }
}