package com.example.healthscope_v03;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CheckboxQuestion extends LinearLayout {

    private String buttonClicked = "Not answered";

    public CheckboxQuestion(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.checkbox_question, this, true);

        String title;
        String number;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CheckboxQuestion, 0, 0);

        try {
            title = a.getString(R.styleable.CheckboxQuestion_questionTitle);
            number = a.getString(R.styleable.CheckboxQuestion_questionNumber);
        } finally {
            a.recycle();
        }

        init(title, number);
    }

    // Setup views
    private void init(String title, String number) {
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        TextView questionNumber = (TextView) findViewById(R.id.question_number);

        questionTitle.setText(title);
        questionNumber.setText(number + ". ");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        CheckBox yes = (CheckBox) findViewById(R.id.yes);
        CheckBox no = (CheckBox) findViewById(R.id.no);

        yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                no.setChecked(false);
                yes.setChecked(true);
                buttonClicked = "1";
            }
        });

        no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                no.setChecked(true);
                yes.setChecked(false);
                buttonClicked = "0";
            }
        });
    }

    String getAnswer(){
        return buttonClicked;
    }

    void setQuestion(String question){
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        questionTitle.setText(question);
    }

    void setQuestionNumber(String number){
        TextView questionTitle = (TextView) findViewById(R.id.question_title);
        questionTitle.setText(number + ". ");
    }
}