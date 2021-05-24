package com.example.healthscope_v03;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFragment;
    SurveyFragment surveyFragment;
    GraphFragment graphFragment;
    FragmentContainerView fragmentContainerView;
    LinearLayout linearLayout;
    SettingsFragment settingsFragment;
    Toolbar myToolbar;
    boolean mainFragmentShowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("HealthScope v1.0");
        myToolbar.setTitleTextColor(Color.GRAY);

        linearLayout = findViewById(R.id.fragmentLinearLayout);

        fragmentContainerView = new FragmentContainerView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        fragmentContainerView.setId(View.generateViewId());
        linearLayout.addView(fragmentContainerView, lp);
        surveyFragment = new SurveyFragment();
        mainFragment = new MainFragment();
        graphFragment = new GraphFragment();
        settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().add(fragmentContainerView.getId(), mainFragment,"MainFragment").addToBackStack("MainFragment").commit();
    }

    public void startMainKillSurvey(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).remove(surveyFragment).show(mainFragment).commit();
        hideBackButton();
        invalidateOptionsMenu();
    }

    public void startMainKillSettings(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).remove(settingsFragment).show(mainFragment).commit();
        hideBackButton();
        invalidateOptionsMenu();
    }
    public void startMainKillGraph(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).remove(graphFragment).show(mainFragment).commit();
        hideBackButton();
        invalidateOptionsMenu();
    }

    public void startGraph(){
        showBackButton();
        graphFragment = new GraphFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(fragmentContainerView.getId(), graphFragment,"GraphFragment").addToBackStack("GraphFragment").hide(mainFragment).show(graphFragment).commit();
        invalidateOptionsMenu();
    }

    public void startSurvey() {
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        if(f!=null && surveyFragment.isVisible())
            return;
        showBackButton();
        if(f!=null && graphFragment.isVisible())
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(fragmentContainerView.getId(),surveyFragment,"SurveyFragment").addToBackStack("Survey Fragment").remove(graphFragment).show(surveyFragment).commit();
        else if(f!=null && settingsFragment.isVisible())
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(fragmentContainerView.getId(),surveyFragment,"SurveyFragment").addToBackStack("Survey Fragment").remove(settingsFragment).show(surveyFragment).commit();
        else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(fragmentContainerView.getId(), surveyFragment, "SurveyFragment").addToBackStack("SurveyFragment").hide(mainFragment).show(surveyFragment).commit();
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mainFragmentShowing){
            Log.d("isVisible", "onCreateOptionsMenu: isn't visible");
            return false;
        }
        Log.d("isVisible", "onCreateOptionsMenu: is visible");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sub_item1) {
            showBackButton();
            settingsFragment = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).hide(mainFragment).add(fragmentContainerView.getId(), settingsFragment,"SettingsFragment").addToBackStack("SettingsFragment").commit();
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showBackButton(){
        mainFragmentShowing = false;
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> {
            if(surveyFragment.isVisible())
                startMainKillSurvey();
            if(graphFragment.isVisible())
                startMainKillGraph();
            if(settingsFragment.isVisible())
                startMainKillSettings();
            //getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right).show(mainFragment).commit();
            hideBackButton();
            invalidateOptionsMenu();
        });
    }

    public void hideBackButton(){
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setNavigationOnClickListener(null);
        mainFragmentShowing = true;
    }
}