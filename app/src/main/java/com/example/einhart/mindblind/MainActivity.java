package com.example.einhart.mindblind;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toast toast;
    private Handler handler;

    private int current_page;
    public static final int NUM_PAGES = 5;

    private boolean showSubtitles;
    private String[] subtitles_l;
    private String[] subtitles_r;
    private TextView subtitle_left;
    private TextView subtitle_right;

    private List<Integer> question_idx;
    private int[] answers;

    private TypedArray pictures;
    private ViewFlipper viewFlipper;

    private ArrayList<ImageView> pageIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        handler = new Handler();

        question_idx = Ints.asList(getResources().getIntArray(R.array.question_idx_array));

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            current_page = 0;
            answers = new int[question_idx.size()];
        } else {
            current_page = extras.getInt("nextPage");
            answers = extras.getIntArray("answers");
        }

        setSubs();

        setButtons();
        addPageInds();

        setViewFlipper();
        setSwipeGestures();

        showPage(current_page);
    }

    private void setSwipeGestures() {
        View view = findViewById(android.R.id.content);
        view.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                turnPage_next();
            }

            @Override
            public void onSwipeRight() {
                turnPage_prev();
            }

            @Override
            public void onSwipeUp() {
                toast.setText("?");
                toast.show();
            }

            @Override
            public void onSwipeDown() {
                toggleSubs();
            }
        });
    }

    private void setViewFlipper() {
        pictures = getResources().obtainTypedArray(R.array.pictures_array);

        viewFlipper = (ViewFlipper) findViewById(R.id.picFlipper);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(pictures.getResourceId(i, 0));
            viewFlipper.addView(imageView, params);
        }

        pictures.recycle();
    }

    /*
    Set up the subtitle texts
     */
    private void setSubs() {
        showSubtitles = true;

        TypedArray testArray = getResources().obtainTypedArray(R.array.versions_subs_r_array);
        int testId = testArray.getResourceId(0, 0);

        subtitles_l = getResources().getStringArray(R.array.subs_l_array);
        subtitles_r = getResources().getStringArray(testId);

        subtitle_left = (TextView) findViewById(R.id.txt_sub_l);
        subtitle_right = (TextView) findViewById(R.id.txt_sub_r);
    }

    private void toggleSubs() {
        showSubtitles = !showSubtitles;
        if (showSubtitles) {
            subtitle_left.setVisibility(View.VISIBLE);
            subtitle_right.setVisibility(View.VISIBLE);
        } else {
            subtitle_left.setVisibility(View.GONE);
            subtitle_right.setVisibility(View.GONE);
        }
    }

    /*
    Set up action listeners for the navigation buttons
     */
    private void setButtons() {
        Button btn_prev = (Button) findViewById(R.id.btn_prev);
        Button btn_next = (Button) findViewById(R.id.btn_next);

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnPage_prev();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnPage_next();
            }
        });
    }

    /*
    Add page indicators dynamically, depending on the number of pages
     */
    private void addPageInds() {
        pageIndicators = new ArrayList<>();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_pageInds);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 50, 1f);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView pageInd = new ImageView(this);
            pageInd.setImageResource(android.R.drawable.radiobutton_off_background);
            linearLayout.addView(pageInd, params);
            pageIndicators.add(pageInd);
        }
    }

    private void turnPage_prev() {
        int old_page = current_page;
        current_page = Math.max(current_page - 1, 0);

        if (current_page != old_page) {
            viewFlipper.setInAnimation(this, R.anim.anim_slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.anim_slide_out_right);
            viewFlipper.showPrevious();
            showPage(current_page);
        }
    }

    private void turnPage_next() {
        int old_page = current_page;
        current_page = current_page + 1;

        /*
        case the next page will be a question
         */
        if (question_idx.contains(old_page)) {

            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("nextPage", current_page);
            intent.putExtra("question", question_idx.indexOf(old_page));
            intent.putExtra("answers", answers);

            startActivity(intent);
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else if (current_page < NUM_PAGES) {
            viewFlipper.setInAnimation(this, R.anim.anim_slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.anim_slide_out_left);
            viewFlipper.showNext();
            showPage(current_page);
        } else {
            endOfPages();
        }

    }

    private void endOfPages() {
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_up, R.anim.anim_slide_out_up);
    }

    private void showPage(int idx) {

        if (idx >= NUM_PAGES) {
            endOfPages();
            return;
        }

        int drawableId;

        viewFlipper.setDisplayedChild(idx);

        for (int i = 0; i < pageIndicators.size(); i++) {
            drawableId = (i == idx) ? (android.R.drawable.radiobutton_on_background) : (android.R.drawable.radiobutton_off_background);
            pageIndicators.get(i).setImageResource(drawableId);
        }

//        subtitle_left.setText(subtitles_l[idx]);
//        subtitle_right.setText(subtitles_r[idx]);

        handler.postDelayed(new TextViewUpdater(subtitles_l[idx], subtitle_left), 400);
        handler.postDelayed(new TextViewUpdater(subtitles_r[idx], subtitle_right), 400);
    }

    private class TextViewUpdater implements Runnable {
        private String mString;
        private TextView mTextView;

        public TextViewUpdater(String string, TextView textView) {
            mString = string;
            mTextView = textView;
        }

        @Override
        public void run() {
            mTextView.setText(mString);
        }
    }

}
