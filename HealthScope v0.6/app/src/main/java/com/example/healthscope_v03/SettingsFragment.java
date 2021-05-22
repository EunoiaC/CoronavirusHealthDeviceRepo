package com.example.healthscope_v03;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        char[] data = new char[6];

        data[0] = 'U';

        Spinner s = requireView().findViewById(R.id.ageSpinner);
        Button submit = requireView().findViewById(R.id.submitSettings);

        YesOrNoQuestion hasDisease, isSmoker;

        MultipleChoiceQuestion gender;

        hasDisease = requireView().findViewById(R.id.hasDisease);
        isSmoker = requireView().findViewById(R.id.isSmoker);
        gender = requireView().findViewById(R.id.gender);

        Bitmap b = BitmapFactory.decodeResource(requireActivity().getResources(),
                R.drawable.blue);

        int num = 175;

        b = Bitmap.createScaledBitmap(b, (int) (num - (num * 0.1)), num, false);

        appendImage(gender.getCheckbox(0), b);

        b = BitmapFactory.decodeResource(requireActivity().getResources(),
                R.drawable.pink);
        b = Bitmap.createScaledBitmap(b, (int) (num - (num * 0.1)), num, false);

        appendImage(gender.getCheckbox(1), b);

        Integer[] choices = new Integer[100];
        for (int i = 0; i < 100; i++){
            choices[i] = i;
        }
        ArrayAdapter<Integer> a = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, choices);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);

        submit.setOnClickListener(v -> {
            if (gender.getSelectedAnswer() == Question.NO_ANSWER || hasDisease.getSelectedAnswer() == Question.NO_ANSWER || isSmoker.getSelectedAnswer() == Question.NO_ANSWER ){
                Toast.makeText(getActivity(), "Please answer all questions.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!((MainActivity) requireActivity()).mainFragment.connectionEstablished) {
                Toast.makeText(getActivity(),"Please go back and establish connection first.",Toast.LENGTH_SHORT).show();
                return;
            }

            data[1] = gender.getSelectedAnswer() == 1 ? '1': '0';
            data[2] = isSmoker.getSelectedAnswer() == 1 ? '1': '0';
            if((Integer) s.getSelectedItem()<10) {
                data[3] = '0';
                data[4] = Character.forDigit((Integer) s.getSelectedItem(), 10);

            }
            else {
                char[] temp;
                temp = String.valueOf(s.getSelectedItem()).toCharArray();
                data[3] = temp[0];
                data[4] = temp[1];
            }

            data[5] = hasDisease.getSelectedAnswer() == 1 ? '1' : '0';


            Log.d("TAG", "onClick: " + Arrays.toString(data));
            Toast.makeText(getActivity(), "User Initialization is Completed.", Toast.LENGTH_SHORT).show();
            ((MainActivity) requireActivity()).mainFragment.Send_data(String.valueOf(data));

            ((MainActivity) requireActivity()).startMainKillSettings();
        });

    }

    private void appendImage(CheckBox checkBox, Bitmap bmp)
    {
        checkBox.setTransformationMethod(null);
        SpannableString ss = new SpannableString("  ");
        ss.setSpan(new ImageSpan(bmp, ImageSpan.ALIGN_BASELINE), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        checkBox.append(ss);
    }
}