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
    private String[] exercises = {"'Flat barbell bench press'", "'Barbell squat'", "'Sumo deadlift' OR exercise = 'Classic deadlift'", "'Pull up'", "'Dip'", "'Overhead press'"};

    TextView benchPressTextView;
    TextView squatTextView;
    TextView deadliftTextView;
    TextView sumTextView;
    TextView pullUpTextView;
    TextView dipTextView;
    TextView ohpTextView;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        dbHelper = MainActivity.dbHelper;
        BottomNavigationView navigationView = findViewById(R.id.exerciseTrackingNavigationBar);
        benchPressTextView = findViewById(R.id.oneRepBenchPress);
        squatTextView = findViewById(R.id.oneRepSquat);
        deadliftTextView = findViewById(R.id.oneRepDeadlift);
        sumTextView = findViewById(R.id.oneRepSum);
        pullUpTextView = findViewById(R.id.oneRepPullUp);
        dipTextView = findViewById(R.id.oneRepDip);
        ohpTextView = findViewById(R.id.oneRepOHP);
        setValues(dbHelper::getBestRep);

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Function<String, Double> getValue;
            switch(item.toString()){
                case "Last calc.":
                    getValue = dbHelper::getCalculatedLastOneRep;
                    break;
                case "Best calc.":
                    getValue = dbHelper::getCalculatedBestOneRep;
                    break;
                default:
                    getValue = dbHelper::getBestRep;
                    break;
            }
            setValues(getValue);
            return true;
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setValues(Function<String, Double> getValue){
        double[] values = new double[exercises.length];
        for(int i = 0; i < values.length; i ++){
            values[i] = getValue.apply(exercises[i]);
            if(values[i] == -1)
                values[i] = 0;
        }
        benchPressTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[0])) + " kg");
        squatTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[1])) + " kg");
        deadliftTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[2])) + " kg");
        sumTextView.setText(BodyWeightActivity.getProperVal(
                String.valueOf(Double.parseDouble(benchPressTextView.getText().toString().substring(0, benchPressTextView.getText().toString().length()-3)) +
                                Double.parseDouble(squatTextView.getText().toString().substring(0, benchPressTextView.getText().toString().length()-3)) +
                                Double.parseDouble(deadliftTextView.getText().toString().substring(0, benchPressTextView.getText().toString().length()-3)))) +
                                " kg");
        pullUpTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[3])) + " kg");
        dipTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[4])) + " kg");
        ohpTextView.setText(BodyWeightActivity.getProperVal(String.valueOf(values[5])) + " kg");

    }
}