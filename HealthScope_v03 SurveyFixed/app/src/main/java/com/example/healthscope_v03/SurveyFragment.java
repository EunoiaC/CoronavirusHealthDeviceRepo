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

import java.util.Arrays;

public class SurveyFragment extends Fragment {

    CheckboxQuestion q1, q2, q3;
    Button submit;
    public char[] finalAnswers = new char[8];


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

                for (int i=0;i<5;i++){
                    finalAnswers[i]='X';
                }

                //Checkbox  1 --> No,   2-->Yes,    3-->NotAnswered.

                if (q1.getAnswer() == 2){
                    finalAnswers[5]='1';   // Char '1' will be sent for answer Yes.
                }
                else{
                    finalAnswers[5]='0';   // Char '0' will be sent for answer No.
                }
                if (q2.getAnswer() == 2){
                    finalAnswers[6]='1';
                }
                else{
                    finalAnswers[6]='0';
                }
                if (q3.getAnswer() == 2){
                    finalAnswers[7]='1';
                }
                else{
                    finalAnswers[7]='0';
                }

                Toast.makeText(getActivity(), "Survey Completed.", Toast.LENGTH_SHORT).show();
                for(int i=0;i<finalAnswers.length;i++)
                {
                    ((MainActivity) getActivity()).mainFragment.Send_data(String.valueOf(finalAnswers[i]));
                }

                ((MainActivity) getActivity()).startMain();
            }
        });
    }



}