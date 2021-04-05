package com.example.healthscope_v03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFragment;
    SurveyFragment surveyFragment;
    GraphFragment graphFragment;
    Button viewGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mainFragment = new MainFragment();
        graphFragment = new GraphFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, mainFragment).commit();
    }

    public void startMain(){
        getSupportFragmentManager().beginTransaction().hide(surveyFragment).show(mainFragment).commit();
    }

    public void startGraph(){
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, graphFragment).hide(mainFragment).commit();
    }

    public void startSurvey() {
        surveyFragment = new SurveyFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, surveyFragment).hide(mainFragment).show(surveyFragment).commit();
    }
}