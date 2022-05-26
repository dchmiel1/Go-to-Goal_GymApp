package com.example.gotogoal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    private TextView emptyTextView;
    private ListView workoutLayout;
    public static int[] multiplier;
    public static SimpleDateFormat dateFormatInDb;
    public Animation slideLeftIn;
    public Animation slideRightIn;
    private TextView dateTextView;
    private Animation slideLeftInFast;
    private Animation slideRightInFast;

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(R.style.AppThemeWithoutBar, true);
        return theme;
    }

    @Override
    public void onBackPressed() {
        System.out.println("BACK PRESSED");
        if(dateTextView.getText() == "Today")
            this.finishAffinity();
        else
            if(new Date().after(date)) {
                setAnimation(slideLeftInFast);
                setAnimation(slideLeftInFast);
                updateDate(System.currentTimeMillis());
                checkWorkout();
            }
            else{
                setAnimation(slideRightInFast);
                setAnimation(slideRightInFast);
                updateDate(System.currentTimeMillis());
                checkWorkout();
            }
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
        emptyTextView = findViewById(R.id.emptyTextView);
        slideLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in);
        slideLeftInFast = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in_fast);
        slideRightIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in);
        slideRightInFast = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_in_fast);

        if(getIntent().hasExtra("date")) {
            try {
                date = dateFormatInDb.parse(getIntent().getExtras().getString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            updateDate(date.getTime());
        }else {
            updateDate(System.currentTimeMillis());
        }

        if(dbHelper == null)
            dbHelper = new DbHelper(this, this);

        checkWorkout();

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
            nextDay();
        });

        leftImageBtn.setOnClickListener(view -> {
            previousDay();
        });
    }

    private void updateDate(long datetime){
        if(date == null)
            date = new Date(datetime);
        date.setTime(datetime);
        String dateText = "";
        if(DateUtils.isToday(datetime))
            dateText = "Today";
        if(DateUtils.isToday(datetime + TimeUnit.DAYS.toMillis(1)))
            dateText = "Yesterday";
        if(DateUtils.isToday(datetime - TimeUnit.DAYS.toMillis(1)))
            dateText = "Tomorrow";
        if(dateText == "")
            dateText = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date);

        dateTextView.setText(dateText);
    }

    public static class Training {
        public String exercise;
        public Vector<String> reps;
        public Vector<String> kgs;
    }

    public void checkWorkout(){
        Cursor c = dbHelper.getSetsByDate();
        Vector<Training> trainings = new Vector<>();
        boolean found = false;
        Training training;
        while(c.moveToNext()){
            for(int i = 0; i < trainings.size(); i ++)
                if (trainings.elementAt(i).exercise.equals(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE)))) {
                    trainings.elementAt(i).reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
                    trainings.elementAt(i).kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
                    found = true;
                }
                if(found) {
                    found = false;
                    continue;
                }
                training = new Training();
                training.exercise = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
                training.reps = new Vector<>();
                training.kgs = new Vector<>();
                training.reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
                training.kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
                trainings.add(training);
            }

        if(trainings.size() == 0)
            emptyTextView.setText("Workout is empty");
        else
            emptyTextView.setText("");

        WorkoutAdapter workoutAdapter = new WorkoutAdapter(this, trainings, this);
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

    private void nextDay() {
        setAnimation(slideLeftIn);
        updateDate(date.getTime() + (1000 * 60 * 60 * 24));
        checkWorkout();
    }

    private void previousDay() {
        setAnimation(slideRightIn);
        updateDate(date.getTime() - (1000 * 60 * 60 * 24));
        checkWorkout();
    }
}