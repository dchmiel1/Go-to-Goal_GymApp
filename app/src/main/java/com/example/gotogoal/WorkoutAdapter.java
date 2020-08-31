package com.example.gotogoal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Vector;

import static android.view.View.VISIBLE;

public class WorkoutAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Vector<MainActivity.Training> trainings;
    private Context c;
    private DbHelper dbHelper;

    public WorkoutAdapter(Context c, Vector<MainActivity.Training> trainings){
        this.trainings = trainings;
        this.c = c;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbHelper = MainActivity.dbHelper;
    }

    @Override
    public int getCount() {
        return trainings.size();
    }

    @Override
    public Object getItem(int i) {
        return trainings.elementAt(i);
    }

    @Override
    public long getItemId(int i) { return 0; }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        long start = System.currentTimeMillis();

        View v = inflater.inflate(R.layout.workout_listview, null);

        TextView exNameTextView = v.findViewById(R.id.exNameTextView);
        ListView listView = v.findViewById(R.id.setsListView);
        ImageView deleteImageView = v.findViewById(R.id.deleteImageView);
        View clickView = v.findViewById(R.id.clickView);
        exNameTextView.setText(trainings.elementAt(i).exercise);

        if(c.getClass() == MainActivity.class) {
            clickView.setOnClickListener(view1 -> updateSets(i));
            clickView.setOnLongClickListener(view1 -> showDelete(v, i));
        }

        WorkoutSetAdapter workoutSetAdapter = new WorkoutSetAdapter(c, trainings.elementAt(i).reps, trainings.elementAt(i).kgs);
        listView.setAdapter(workoutSetAdapter);
        deleteImageView.setOnClickListener(view1 -> dbHelper.deleteByDateAndExercise(trainings.elementAt(i).exercise));

        ConstraintLayout.LayoutParams mParam = new ConstraintLayout.LayoutParams(-1, (int)(exNameTextView.getTextSize()*1.9) + (int)(exNameTextView.getTextSize() *(1.85* trainings.elementAt(i).reps.size())));
        v.setLayoutParams(mParam);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
        return v;
    }

    public void updateSets(int i) {
        Intent showRepsAndKgsActivity = new Intent(c, WorkoutTrackActivity.class);
        showRepsAndKgsActivity.putExtra("ex_name", trainings.elementAt(i).exercise);
        c.startActivity(showRepsAndKgsActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean showDelete(View v, int i){
        v.findViewById(R.id.deleteImageView).setVisibility(VISIBLE);
        v.findViewById(R.id.clickView).setOnClickListener(view -> hideDelete(v, i));
        v.setBackgroundColor(Color.parseColor("#9C9691"));
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideDelete(View v, int i){
        v.findViewById(R.id.deleteImageView).setVisibility(View.GONE);
        v.findViewById(R.id.clickView).setOnClickListener(view -> updateSets(i));
        v.setBackground(c.getResources().getDrawable(R.drawable.border_dark_orange, null));
    }

}
