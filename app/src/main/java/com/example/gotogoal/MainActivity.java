package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    private TextView emptyTextView;
    private GridView workoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
        ImageView addImageView = (ImageView) findViewById(R.id.addImageView);
        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        ImageButton rightImageBtn = (ImageButton) findViewById(R.id.rightImageBtn);
        ImageButton leftImageBtn = (ImageButton) findViewById(R.id.leftImageBtn);
        workoutLayout = (GridView) findViewById(R.id.workoutGridView);
        final Animation slideLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in);
        final Animation slideRightIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in);
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        date = new Date();
        dateTextView.setText("Today");

        if(dbHelper == null) {
            dbHelper = new DbHelper(this);
        }

        checkWorkout();
        dbHelper.showAll();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(showProfileIntent);
            }
        });

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showMusclesIntent = new Intent(getApplicationContext(), MusclesActivity.class);
                startActivity(showMusclesIntent);
            }
        });

        rightImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutLayout.startAnimation(slideLeftIn);
                emptyTextView.startAnimation(slideLeftIn);
                date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
                setDate(dateTextView);
                checkWorkout();
            }
        });

        leftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutLayout.startAnimation(slideRightIn);
                emptyTextView.startAnimation(slideRightIn);
                date = new Date(date.getTime() - (1000 * 60 * 60 * 24));
                setDate(dateTextView);
                checkWorkout();
            }
        });
    }

    private void setDate(TextView dateTextView){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        c1.setTime(date);
        int dayC1= c1.get(Calendar.DAY_OF_MONTH);
        int monthC1 = c1.get(Calendar.MONTH);
        int yearC1 = c1.get(Calendar.YEAR);
        int dayC2= c2.get(Calendar.DAY_OF_MONTH);
        int monthC2 = c2.get(Calendar.MONTH);
        int yearC2 = c2.get(Calendar.YEAR);
        if(monthC1 == monthC2 && yearC1 == yearC2) {
            if (dayC1 == dayC2) {
                dateTextView.setText("Today");
            } else
                if (dayC1 == dayC2 + 1)
                    dateTextView.setText("Tomorrow");
                else
                    if (dayC1 == dayC2 - 1)
                        dateTextView.setText("Yesterday");
                    else
                        dateTextView.setText(new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date));
        }else
            dateTextView.setText(new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date));
    }

    private void checkWorkout(){
        Cursor c = dbHelper.getByDate(new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date));
        int i = 0;
        int howMany = c.getCount();
        String[] exercises = new String[howMany];
        if(howMany == 0){
            emptyTextView.setText("Workout is empty");
        }
        else{
            emptyTextView.setText("");
        }
        while(c.moveToNext()) {
            exercises[i] = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
            ++i;
        }
        WorkoutAdapter workoutAdapter = new WorkoutAdapter(this, exercises);
        workoutLayout.setAdapter(workoutAdapter);
    }
}