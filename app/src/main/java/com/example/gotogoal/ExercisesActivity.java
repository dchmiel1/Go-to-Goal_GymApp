package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExercisesActivity extends AppCompatActivity {

    String exercises[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        ListView listView = (ListView) findViewById(R.id.listView);
        Resources res = getResources();
        int id = getIntent().getExtras().getInt("muscleId");
        switch(id){
            case 0:
                exercises = res.getStringArray(R.array.chest_ex);
                break;
            case 1:
                exercises = res.getStringArray(R.array.back_ex);
                break;
            case 2:
                exercises = res.getStringArray(R.array.shoulders_ex);
                break;
            case 3:
                exercises = res.getStringArray(R.array.biceps_ex);
                break;
            case 4:
                exercises = res.getStringArray(R.array.triceps_ex);
                break;
            case 5:
                exercises = res.getStringArray(R.array.legs_ex);
                break;
            case 6:
                exercises = res.getStringArray(R.array.abs_ex);
                break;
            default:
                exercises = null;
                break;
        }

        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.exercise_listview, exercises));

    }
}