package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExercisesActivity extends AppCompatActivity {

    private String[] exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        final ListView listView = findViewById(R.id.listView);
        Resources res = getResources();
        if(getIntent().hasExtra("muscleChosen") && getIntent().getExtras().getString("muscleChosen") != null) {
                switch (getIntent().getExtras().getString("muscleChosen")) {
                    case "Chest":
                        exercises = res.getStringArray(R.array.chest_ex);
                        break;
                    case "Back":
                        exercises = res.getStringArray(R.array.back_ex);
                        break;
                    case "Shoulders":
                        exercises = res.getStringArray(R.array.shoulders_ex);
                        break;
                    case "Biceps":
                        exercises = res.getStringArray(R.array.biceps_ex);
                        break;
                    case "Triceps":
                        exercises = res.getStringArray(R.array.triceps_ex);
                        break;
                    case "Legs":
                        exercises = res.getStringArray(R.array.legs_ex);
                        break;
                    case "ABS":
                        exercises = res.getStringArray(R.array.abs_ex);
                        break;
                    default:
                        exercises = res.getStringArray(R.array.buttocks_ex);
                        break;
                }
        }

        listView.setAdapter(new ArrayAdapter<>(this, R.layout.exercise_listview, exercises));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent showRepsAndKgsActivity = new Intent(getApplicationContext(), WorkoutTrackActivity.class);
            showRepsAndKgsActivity.putExtra("ex_name", listView.getItemAtPosition(i).toString());
            startActivity(showRepsAndKgsActivity);
        });

    }
}