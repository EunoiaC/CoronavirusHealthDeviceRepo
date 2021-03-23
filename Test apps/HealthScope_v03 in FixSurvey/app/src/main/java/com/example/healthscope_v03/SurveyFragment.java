package com.example.healthscope_v03;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.healthscope_v03.CustomViews.CheckboxQuestion;

public class SurveyFragment extends Fragment {

    CheckboxQuestion q1, q2, q3;
    Button submit;
    public int finalAnswers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_survey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        q1 = getView().findViewById(R.id.question1);
        q2 = getView().findViewById(R.id.question2);
        q3 = getView().findViewById(R.id.question3);
        submit = getView().findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (q1.getAnswer() == 3 || q2.getAnswer() == 3 || q3.getAnswer() == 3) {
                    Toast.makeText(getActivity(), "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getActivity(), "Survey Completed.", Toast.LENGTH_SHORT).show();
                finalAnswers = 0;
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

                ((MainActivity) getActivity()).mainFragment.Send_data(String.valueOf(finalAnswers));
                ((MainActivity) getActivity()).startMain();
            }
        });
    }



}