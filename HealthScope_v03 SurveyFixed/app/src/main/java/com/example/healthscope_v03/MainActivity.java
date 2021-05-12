package com.example.healthscope_v03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFragment;
    SurveyFragment surveyFragment;
    GraphFragment graphFragment;
    FragmentContainerView fragmentContainerView;
    LinearLayout linearLayout;
    Button viewGraph;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.fragmentLinearLayout);
        settingsFragment = new SettingsFragment();
        fragmentContainerView = new FragmentContainerView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        fragmentContainerView.setId(View.generateViewId());
        linearLayout.addView(fragmentContainerView, lp);

        mainFragment = new MainFragment();
        graphFragment = new GraphFragment();

        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), mainFragment).commit();
    }

    public void startMainKillSurvey(){
        getSupportFragmentManager().beginTransaction().remove(surveyFragment).show(mainFragment).commit();
    }

    public void startMainKillSettings(){
        getSupportFragmentManager().beginTransaction().remove(settingsFragment).show(mainFragment).commit();
    }

    public void startGraph(){
        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), graphFragment).hide(mainFragment).commit();
    }

    public void startSurvey() {
        surveyFragment = new SurveyFragment();
        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), surveyFragment).hide(mainFragment).show(surveyFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sub_item1:
                settingsFragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), settingsFragment).hide(mainFragment).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}