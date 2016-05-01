package com.example.einhart.mindblind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EndActivity extends AppCompatActivity {

    private TextView score;
    private TextView result;

    private int[] questions_idx;
    private int[] answers_user;
    private int[] answers_correct;
    private Toast toast;

    private String getTotalScore() {
        answers_correct = getResources().getIntArray(R.array.answers_array);
        int result = 0;
        int total = 0;

        for (int i = 0; i < answers_correct.length; i++) {
            if (questions_idx[i] != -1) {
                if (answers_user[i] == answers_correct[i]) {
                    result++;
                }
                total++;
            }
        }

        return result + "/" + total;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        result = (TextView) findViewById(R.id.txt_result);

        questions_idx = getResources().getIntArray(R.array.question_idx_array);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            answers_user = extras.getIntArray("answers");
        } else {
            answers_user = null;
        }

        score = (TextView) findViewById(R.id.txt_score);
        score.setText(getTotalScore());

        showFeedbacks();
    }

    private void showFeedbacks() {
        String[] questions = getResources().getStringArray(R.array.question_txt_array);
        String[] feedBacks = new String[questions.length];

        String[] feedBacks_correct = getResources().getStringArray(R.array.feedback_correct_array);
        String[] feedBacks_wrong = getResources().getStringArray(R.array.feedback_incorrect_array);

        answers_correct = getResources().getIntArray(R.array.answers_array);

        int q = 0;
        for (int i = 0; i < answers_correct.length; i++) {
            if(questions_idx[i] != -1) {
                q ++;
                if (answers_user[i] == answers_correct[i]) {
                    feedBacks[i] = (i+1) + "." + questions[i] + " Answered correctly: " + feedBacks_correct[i];
                } else {
                    feedBacks[i] = (i+1) + "." + questions[i] + " Answered incorrectly: " + feedBacks_wrong[i];
                }
            } else {
                feedBacks[i] = (i+1) + "." + "-Question deactivated-";
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.endactivity_listview, feedBacks);
        ListView listView = (ListView) findViewById(R.id.feedback_list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_title) {
            Intent intent = new Intent(this, TitleActivity.class);
            startActivity(intent);

            this.overridePendingTransition(R.anim.anim_slide_in_down, R.anim.anim_slide_out_down);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
