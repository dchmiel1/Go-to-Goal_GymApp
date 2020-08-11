package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    private String exercises[] = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
        ImageView addImageView = (ImageView) findViewById(R.id.addImageView);
        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        ImageButton rightImageBtn = (ImageButton) findViewById(R.id.rightImageBtn);
        ImageButton leftImageBtn = (ImageButton) findViewById(R.id.leftImageBtn);
        final GridView layout = (GridView) findViewById(R.id.workoutGridView);
        final Animation slideLeftOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_out);
        final Animation slideRightOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_out);
        final TextView emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        date = new Date();
        dateTextView.setText("Today");

        if(dbHelper == null) {
            System.out.println("Utworzono");
            dbHelper = new DbHelper(this);
        }
        Cursor c = dbHelper.getByDate(new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date));
        int i = 0;
        if(c.getCount() == 0){
            emptyTextView.setText("Workout is empty");
        }
        while(c.moveToNext()) {
            System.out.println("znalazl");
            exercises[i] = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
            ++i;
        }
        dbHelper.showAll();

        WorkoutAdapter workoutAdapter = new WorkoutAdapter(this, exercises);
        layout.setAdapter(workoutAdapter);

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
                layout.startAnimation(slideLeftOut);
                emptyTextView.startAnimation(slideLeftOut);
                date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
                setDate(dateTextView);
            }
        });

        leftImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.startAnimation(slideRightOut);
                emptyTextView.startAnimation(slideRightOut);
                date = new Date(date.getTime() - (1000 * 60 * 60 * 24));
                setDate(dateTextView);
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
}