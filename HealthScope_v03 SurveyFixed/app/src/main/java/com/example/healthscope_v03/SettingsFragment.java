package com.example.healthscope_v03;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.aadyad.checkboxquestion.Question;
import com.aadyad.checkboxquestion.Views.MultipleChoiceQuestion;
import com.aadyad.checkboxquestion.Views.YesOrNoQuestion;

import java.util.Arrays;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        char[] data = new char[24];

        data[0] = 'U';

        Spinner s = getView().findViewById(R.id.ageSpinner);
        Button submit = getView().findViewById(R.id.submitSettings);

        MultipleChoiceQuestion gender, hasDisease, isSmoker;

        hasDisease = getView().findViewById(R.id.hasDisease);
        isSmoker = getView().findViewById(R.id.isSmoker);
        gender = getView().findViewById(R.id.gender);

        Integer[] choices = new Integer[100];
        for (int i = 0; i < 100; i++){
            choices[i] = i;
        }
        ArrayAdapter<Integer> a = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, choices);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.getSelectedAnswer() == Question.NO_ANSWER || hasDisease.getSelectedAnswer() == Question.NO_ANSWER || isSmoker.getSelectedAnswer() == Question.NO_ANSWER ){
                    Toast.makeText(getActivity(), "Please answer all questions.", Toast.LENGTH_SHORT).show();
                    return;
                }

                data[1] = gender.getSelectedAnswer() == 1 ? '1': '0';
                data[2] = isSmoker.getSelectedAnswer() == 1 ? '1': '0';
                data[3] = (char) s.getSelectedItem();
                data[4] = hasDisease.getSelectedAnswer() == 1 ? '1' : '0';

                Log.d("TAG", "onClick: " + Arrays.toString(data));
            }
        });

    }
}