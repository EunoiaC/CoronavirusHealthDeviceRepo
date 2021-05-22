package com.example.healthscope_v03.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.healthscope_v03.R;

public class CheckboxQuestion extends LinearLayout {

    private int buttonClicked = 3;

    public CheckboxQuestion(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.checkbox_question, this, true);

        String title;
        String number;
        boolean numberEnabled;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CheckboxQuestion, 0, 0);

        try {
            title = a.getString(R.styleable.CheckboxQuestion_questionTitle);
            number = a.getString(R.styleable.CheckboxQuestion_questionNumber);
            numberEnabled = a.getBoolean(R.styleable.CheckboxQuestion_numberEnabled, true);
        } finally {
            a.recycle();
        }

        init(title, number, numberEnabled);
    }

    // Setup views
    @SuppressLint("SetTextI18n")
    private void init(String title, String number, boolean numEnabled) {
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        TextView questionNumber = (TextView) findViewById(R.id.question_number);

        questionTitle.setText(title);
        if (numEnabled){
            questionNumber.setText(number + ". ");
            questionNumber.setVisibility(VISIBLE);
        } else{
            questionNumber.setVisibility(GONE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        CheckBox yes = (CheckBox) findViewById(R.id.yes);
        CheckBox no = (CheckBox) findViewById(R.id.no);

        yes.setOnClickListener(v -> {
            no.setChecked(false);
            yes.setChecked(true);
            buttonClicked = 2;
        });

        no.setOnClickListener(v -> {
            no.setChecked(true);
            yes.setChecked(false);
            buttonClicked = 1;
        });
    }
/*
    public int getAnswer(){
        return buttonClicked;
    }

    public void setQuestion(String question){
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        questionTitle.setText(question);
    }

    public void setQuestionNumber(String number){
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        questionTitle.setText(number + ". ");
    }

 */
}