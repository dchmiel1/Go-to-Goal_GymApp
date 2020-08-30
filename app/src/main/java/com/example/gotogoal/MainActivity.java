package com.example.gotogoal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    private TextView emptyTextView;
    private ListView workoutLayout;
    public static int[] multiplier;
    public static SimpleDateFormat dateFormatInDb;
    private TextView dateTextView;

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(R.style.AppThemeWithoutBar, true);
        return theme;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormatInDb = new SimpleDateFormat("yyyy.MM.dd");
        dateTextView = findViewById(R.id.dateTextView);
        multiplier = getResources().getIntArray(R.array.multipliers);
        ImageView profileImageView = findViewById(R.id.profileImageView);
        ImageView addImageView = findViewById(R.id.addImageView);
        ImageView achievementsImageView = findViewById(R.id.achievementsImageView);
        ImageView graphsImageView = findViewById(R.id.graphsImageView);
        ImageView rightImageBtn = findViewById(R.id.rightImageBtn);
        ImageView leftImageBtn = findViewById(R.id.leftImageBtn);
        workoutLayout = findViewById(R.id.workoutListView);
        final Animation slideLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in);
        final Animation slideRightIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in);
        emptyTextView = findViewById(R.id.emptyTextView);
        if(getIntent().hasExtra("date")) {
            try {
                date = dateFormatInDb.parse(getIntent().getExtras().getString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setDate();
        }else {
            date = new Date();
            dateTextView.setText("Today");
        }

        if(dbHelper == null) {
            dbHelper = new DbHelper(this, this);
        }
        checkWorkout();
        dbHelper.showAll();

        graphsImageView.setOnClickListener(view -> {
            Intent showGraphsActivityIntent = new Intent(getApplicationContext(), GraphsActivity.class);
            startActivity(showGraphsActivityIntent);
        });

        achievementsImageView.setOnClickListener(view -> {
            Intent showAchievementsIntent = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(showAchievementsIntent);
        });

        profileImageView.setOnClickListener(view -> {
            Intent showProfileIntent = new Intent(getApplicationContext(), BodyWeightActivity.class);
            startActivity(showProfileIntent);
        });

        addImageView.setOnClickListener(view -> {
            Intent showMusclesIntent = new Intent(getApplicationContext(), MusclesActivity.class);
            startActivity(showMusclesIntent);
        });

        rightImageBtn.setOnClickListener(view -> {
            setAnimation(slideLeftIn);
            date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
            setDate();
            checkWorkout();
        });

        leftImageBtn.setOnClickListener(view -> {
            setAnimation(slideRightIn);
            date = new Date(date.getTime() - (1000 * 60 * 60 * 24));
            setDate();
            checkWorkout();
        });
    }

    private void setDate(){
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

    public static class Training {
        public String exercise;
        public String[] reps;
        public String[] kgs;
        Training(){
        }
    }

    public void checkWorkout(){
        String dateString = dateFormatInDb.format(date);
        Cursor c2 = dbHelper.getExercisesByDate(dateString);
        int howMany = c2.getCount();
        Training[] trainings = new Training[howMany];

        for(int k = 0; k < howMany; k++){
            trainings[k] = new Training();
        }

        String[] exercises = new String[howMany];
        if(howMany == 0)
            emptyTextView.setText("Workout is empty");
        else
            emptyTextView.setText("");
        int i = 0;
        while(c2.moveToNext()) {
            exercises[i] = c2.getString(c2.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
            trainings[i].exercise = exercises[i];
            ++i;
        }

        for(int j = 0; j < howMany; j++) {
            Cursor c3 = dbHelper.getSetsByDateAndExercise(dateString, exercises[j]);
            trainings[j].reps = new String[c3.getCount()];
            trainings[j].kgs = new String[c3.getCount()];
            int k = 0;
            while (c3.moveToNext()) {
                trainings[j].reps[k] = String.valueOf(c3.getInt(c3.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)));
                trainings[j].kgs[k] = String.valueOf(c3.getDouble(c3.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
                ++k;
            }
        }
        WorkoutAdapter workoutAdapter = new WorkoutAdapter(this, trainings);
        workoutLayout.setAdapter(workoutAdapter);
    }

    private void setAnimation(Animation anim){
        workoutLayout.startAnimation(anim);
        emptyTextView.startAnimation(anim);
        findViewById(R.id.hDiv1).startAnimation(anim);
        findViewById(R.id.hDiv2).startAnimation(anim);
        findViewById(R.id.vDiv1).startAnimation(anim);
        findViewById(R.id.vDiv2).startAnimation(anim);
    }
}