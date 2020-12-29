package com.example.healthscope_v03;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
                if (q1.getAnswer().equals("Not answered") || q2.getAnswer().equals("Not answered") || q3.getAnswer().equals("Not answered")){
                    Toast.makeText(SurveyActivity.this, "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SurveyActivity.this, "Survey Completed.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SurveyActivity.this, MainActivity.class);
                i.putExtra("Question 1", q1.getAnswer());
                i.putExtra("Question 2", q2.getAnswer());
                i.putExtra("Question 3", q3.getAnswer());
                startActivity(i);
            }
        });
    }
}