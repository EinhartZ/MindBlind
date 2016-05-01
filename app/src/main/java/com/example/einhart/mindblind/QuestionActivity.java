package com.example.einhart.mindblind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionActivity extends AppCompatActivity {

    private Toast toast;

    private int question_nr;
    private int nextPage;
    private int[] answers;

    private ImageView answer_l;
    private ImageView answer_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        setAnswers();
    }

    private void setAnswers() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            question_nr = 0;
        } else {
            question_nr = extras.getInt("question");
            nextPage = extras.getInt("nextPage");
            answers = extras.getIntArray("answers");
        }

        String txt = getResources().getStringArray(R.array.question_txt_array)[question_nr];
        int img_l_id = getResources().obtainTypedArray(R.array.answerImg_l_array).getResourceId(question_nr, 0);
        int img_r_id = getResources().obtainTypedArray(R.array.answerImg_r_array).getResourceId(question_nr, 0);

        ((TextView) findViewById(R.id.txt_question)).setText(txt);
        ((ImageView) findViewById(R.id.answer_l)).setImageResource(img_l_id);
        ((ImageView) findViewById(R.id.answer_r)).setImageResource(img_r_id);

        answer_l = (ImageView) findViewById(R.id.answer_l);
        answer_r = (ImageView) findViewById(R.id.answer_r);

        answer_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAnswer(1);
            }
        });
        answer_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAnswer(2);
            }
        });
    }

    private void registerAnswer(int idx) {
        answers[question_nr] = idx;

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nextPage", nextPage);
//        intent.putExtra("question", question_nr);
        intent.putExtra("answers", answers);
        startActivity(intent);

        this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
