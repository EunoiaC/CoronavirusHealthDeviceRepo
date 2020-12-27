package com.example.healthscope_v03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                Toast.makeText(SurveyActivity.this, "Question 1 answer " + q1.getAnswer(), Toast.LENGTH_SHORT).show();
                Toast.makeText(SurveyActivity.this, "Question 2 answer " + q2.getAnswer(), Toast.LENGTH_SHORT).show();
                Toast.makeText(SurveyActivity.this, "Question 3 answer " + q3.getAnswer(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}