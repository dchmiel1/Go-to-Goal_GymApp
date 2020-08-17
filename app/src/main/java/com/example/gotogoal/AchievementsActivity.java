package com.example.gotogoal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.function.Function;

public class AchievementsActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private String[] exercises = {"Flat barbell bench press", "Barbell squat", "Sumo deadlift", "Classic deadlift", "Pull up", "Dip"};

    TextView benchPressTextView;
    TextView squatTextView;
    TextView deadliftTextView;
    TextView sumTextView;
    TextView pullUpTextView;
    TextView dipTextView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        dbHelper = MainActivity.dbHelper;
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigationBar);
        benchPressTextView = (TextView) findViewById(R.id.oneRepBenchPress);
        squatTextView = (TextView) findViewById(R.id.oneRepSquat);
        deadliftTextView = (TextView) findViewById(R.id.oneRepDeadlift);
        sumTextView = (TextView) findViewById(R.id.oneRepSum);
        pullUpTextView = (TextView) findViewById(R.id.oneRepPullUp);
        dipTextView = (TextView) findViewById(R.id.oneRepDip);
        setValues(dbHelper::getLastOneRep);

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Function<String, Double> getValue;
            switch(item.toString()){
                case "Done last":
                    getValue = dbHelper::getLastOneRep;
                    break;
                case "Last calc.":
                    getValue = dbHelper::getCalculatedLastOneRep;
                    break;
                case "Best calc.":
                    getValue = dbHelper::getCalculatedBestOneRep;
                    break;
                default:
                    getValue = dbHelper::getBestOneRep;
                    break;
            }
            setValues(getValue);
            return true;
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setValues(Function<String, Double> getValue){
        double[] values = new double[6];
        for(int i = 0; i < values.length; i ++){
            values[i] = getValue.apply(exercises[i]);
            if(values[i] == -1)
                values[i] = 0;
        }
        benchPressTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[0])));
        squatTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[1])));
        if(values[2] > values[3])
            deadliftTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[2])));
        else
            deadliftTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[3])));
        sumTextView.setText(ProfileActivity.getProperVal(String.valueOf(Double.parseDouble(benchPressTextView.getText().toString()) + Double.parseDouble(squatTextView.getText().toString()) + Double.parseDouble(deadliftTextView.getText().toString()))));
        pullUpTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[4])));
        dipTextView.setText(ProfileActivity.getProperVal(String.valueOf(values[5])));
    }
}