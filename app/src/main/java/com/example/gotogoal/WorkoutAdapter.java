package com.example.gotogoal;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

public class WorkoutAdapter extends BaseAdapter{

    String[]  exercises;
    LayoutInflater inflater;
    ListView adapterListView[];
    MainActivity.Structure structures[];
    Context c;

    public WorkoutAdapter(Context c, MainActivity.Structure[] structures){
        this.structures = structures;
        this.c = c;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return structures.length;
    }

    @Override
    public Object getItem(int i) {
        return structures[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.workout_listview, null);

        TextView exNameTextView = (TextView) v.findViewById(R.id.exNameTextView);
        ListView listView = (ListView) v.findViewById(R.id.setsListView);
        RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams((int)(975),(int)(148 + 3 + 3 + structures[i].reps.length * 126));
        v.setLayoutParams(mParam);
        listView.setAdapter(new ArrayAdapter<String>(c, R.layout.exercise_in_workout_listview, new String[0]));
        exNameTextView.setOnClickListener(view1 -> updateSets(i));
        listView.setOnItemClickListener((adapterView, view1, i1, l) -> updateSets(i));

        for(int j = 0; j < structures[i].reps.length; j ++)
            listView.addFooterView(newListViewItem(structures[i].reps[j], structures[i].kgs[j]));
        exNameTextView.setText(structures[i].exercise);
        return v;
    }

    private View newListViewItem(String reps, String kgs){
        View v = inflater.inflate(R.layout.exercise_in_workout_listview, null);
        TextView repsTextView = (TextView) v.findViewById(R.id.repsTextView);
        TextView kgsTextView = (TextView) v.findViewById(R.id.kgsTextView);
        repsTextView.setText(ProfileActivity.getProperVal(reps));
        kgsTextView.setText(ProfileActivity.getProperVal(kgs));
        return v;
    }

    public void updateSets(int i) {
        Intent showRepsAndKgsActivity = new Intent(c, RepsAndKgsActivity.class);
        showRepsAndKgsActivity.putExtra("ex_name", structures[i].exercise);
        c.startActivity(showRepsAndKgsActivity);
    }

    public boolean showDelete(){
        System.out.println("Clicked");
        return true;
    }
}
