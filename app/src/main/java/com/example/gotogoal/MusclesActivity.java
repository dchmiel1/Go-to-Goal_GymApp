package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.lang.reflect.Array;

public class MusclesActivity extends AppCompatActivity {

    String muscles[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);
        Resources res = getResources();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        muscles = res.getStringArray(R.array.muscles);
        MuscleAdapter muscleAdapter = new MuscleAdapter(this, muscles);
        gridView.setAdapter(muscleAdapter);

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent showExercisesIntent = new Intent(getApplicationContext(), ExercisesActivity.class);
                showExercisesIntent.putExtra("muscleId", i);
                startActivity(showExercisesIntent);
        });
    }
}