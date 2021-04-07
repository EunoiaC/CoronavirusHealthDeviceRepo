package com.example.healthscope_v03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFragment;
    SurveyFragment surveyFragment;
    GraphFragment graphFragment;
    FragmentContainerView fragmentContainerView;
    LinearLayout linearLayout;
    Button viewGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        linearLayout = findViewById(R.id.fragmentLinearLayout);
        fragmentContainerView = new FragmentContainerView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        fragmentContainerView.setId(11223);
        linearLayout.addView(fragmentContainerView, lp);

        mainFragment = new MainFragment();
        graphFragment = new GraphFragment();

        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), mainFragment).commit();
    }

    public void startMain(){
        getSupportFragmentManager().beginTransaction().hide(surveyFragment).show(mainFragment).commit();
    }

    public void startGraph(){
        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), graphFragment).hide(mainFragment).commit();
    }

    public void startSurvey() {
        surveyFragment = new SurveyFragment();
        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), surveyFragment).hide(mainFragment).show(surveyFragment).commit();
    }
}