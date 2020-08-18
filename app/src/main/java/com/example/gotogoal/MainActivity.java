package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    private TextView emptyTextView;
    private ListView workoutLayout;
    private LayoutInflater inflater;
    public static int multiplier[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        multiplier = getResources().getIntArray(R.array.multipliers);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView profileImageView = (ImageView) findViewById(R.id.profileImageView);
        ImageView addImageView = (ImageView) findViewById(R.id.addImageView);
        ImageView achievementsImageView = (ImageView) findViewById(R.id.achievementsImageView);
        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        ImageButton rightImageBtn = (ImageButton) findViewById(R.id.rightImageBtn);
        ImageButton leftImageBtn = (ImageButton) findViewById(R.id.leftImageBtn);
        workoutLayout = (ListView) findViewById(R.id.workoutListView);
        final Animation slideLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in);
        final Animation slideRightIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in);
        emptyTextView = (TextView) findViewById(R.id.emptyTextView);
        date = new Date();
        dateTextView.setText("Today");

        workoutLayout.setOnItemLongClickListener((adapterView, view, i, l) -> showDelete());
        workoutLayout.setOnItemClickListener((adapterView, view, i, l) -> updateSets());

        if(dbHelper == null) {
            dbHelper = new DbHelper(this);
        }

        checkWorkout();
        dbHelper.showAll();

        achievementsImageView.setOnClickListener(view -> {
            Intent showAchievementsIntent = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(showAchievementsIntent);
        });

        profileImageView.setOnClickListener(view -> {
            Intent showProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(showProfileIntent);
        });

        addImageView.setOnClickListener(view -> {
            Intent showMusclesIntent = new Intent(getApplicationContext(), MusclesActivity.class);
            startActivity(showMusclesIntent);
        });

        rightImageBtn.setOnClickListener(view -> {
            workoutLayout.startAnimation(slideLeftIn);
            emptyTextView.startAnimation(slideLeftIn);
            date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
            setDate(dateTextView);
            checkWorkout();
        });

        leftImageBtn.setOnClickListener(view -> {
            workoutLayout.startAnimation(slideRightIn);
            emptyTextView.startAnimation(slideRightIn);
            date = new Date(date.getTime() - (1000 * 60 * 60 * 24));
            setDate(dateTextView);
            checkWorkout();
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

    public class Structure{
        public String exercise;
        public String[] reps;
        public String[] kgs;
        Structure(){

        }
    }

    private void checkWorkout(){
        String dateString = new SimpleDateFormat("yyyy MM dd", Locale.getDefault()).format(date);
        Cursor c2 = dbHelper.getExercisesByDate(dateString);
        int howMany = c2.getCount();
        Structure[] structures = new Structure[howMany];

        for(int k = 0; k < howMany; k++){
            structures[k] = new Structure();
        }

        String[] exercises = new String[howMany];
        if(howMany == 0)
            emptyTextView.setText("Workout is empty");
        else
            emptyTextView.setText("");
        int i = 0;
        while(c2.moveToNext()) {
            exercises[i] = c2.getString(c2.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
            structures[i].exercise = exercises[i];
            ++i;
        }

        for(int j = 0; j < howMany; j++) {
            Cursor c3 = dbHelper.getSetsByDateAndExercise(dateString, exercises[j]);
            structures[j].reps = new String[c3.getCount()];
            structures[j].kgs = new String[c3.getCount()];
            int k = 0;
            while (c3.moveToNext()) {
                structures[j].reps[k] = String.valueOf(c3.getInt(c3.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)));
                structures[j].kgs[k] = String.valueOf(c3.getDouble(c3.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
                ++k;
            }
        }
        WorkoutAdapter workoutAdapter = new WorkoutAdapter(this, structures);
        workoutLayout.setAdapter(workoutAdapter);
    }

    public boolean showDelete(){
        System.out.println("Clicked from layout");
        return true;
    }

    public void updateSets(){
        System.out.println("Clicked once");
    }
}