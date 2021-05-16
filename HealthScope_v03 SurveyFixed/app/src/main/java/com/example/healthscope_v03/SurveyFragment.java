package com.example.healthscope_v03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aadyad.checkboxquestion.Question;
import com.aadyad.checkboxquestion.Views.YesOrNoQuestion;

public class SurveyFragment extends Fragment {

    YesOrNoQuestion q1, q2, q3;
    Button submit;
    public char[] finalAnswers = new char[4];


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_survey, container, false);
    }

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        q1 = requireView().findViewById(R.id.question1);
        q2 = requireView().findViewById(R.id.question2);
        q3 = requireView().findViewById(R.id.question3);
        submit = requireView().findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            if (q1.getSelectedAnswer() == Question.NO_ANSWER || q2.getSelectedAnswer() == Question.NO_ANSWER || q3.getSelectedAnswer() == Question.NO_ANSWER) {
                Toast.makeText(getActivity(), "Please answer all the questions.", Toast.LENGTH_SHORT).show();
                return;
            }

            finalAnswers[0]='S';

            //Checkbox  1 --> No,   2-->Yes,    3-->NotAnswered.

            if (q1.getSelectedAnswer() == 1){
                finalAnswers[1]='1';   // Char '1' will be sent for answer Yes.
            }
            else{
                finalAnswers[1]='0';   // Char '0' will be sent for answer No.
            }
            if (q2.getSelectedAnswer() == 1){
                finalAnswers[2]='1';
            }
            else{
                finalAnswers[2]='0';
            }
            if (q3.getSelectedAnswer() == 1){
                finalAnswers[3]='1';
            }
            else{
                finalAnswers[3]='0';
            }

            Toast.makeText(getActivity(), "Survey Completed.", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).mainFragment.Send_data(String.valueOf(finalAnswers));
            ((MainActivity) requireActivity()).startMainKillSurvey();
        });
    }



}