package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.GridView;

public class MusclesActivity extends AppCompatActivity {

    String[] muscles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);
        Resources res = getResources();
        GridView gridView = findViewById(R.id.gridView);
        muscles = res.getStringArray(R.array.muscles);
        MuscleAdapter muscleAdapter = new MuscleAdapter(this, muscles);
        gridView.setAdapter(muscleAdapter);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent showExercisesIntent = new Intent(getApplicationContext(), ExercisesActivity.class);
                showExercisesIntent.putExtra("muscleChosen", muscleAdapter.getItem(i).toString());
                startActivity(showExercisesIntent);
        });
    }
}