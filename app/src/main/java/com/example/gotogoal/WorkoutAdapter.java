package com.example.gotogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WorkoutAdapter extends BaseAdapter {

    String[]  exercises;
    LayoutInflater inflater;

    public WorkoutAdapter(Context c, String[] exercises){
        this.exercises = exercises;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return exercises.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.workout_gridview, null);
        TextView exNameTextView = (TextView) v.findViewById(R.id.exNameTextView);
        exNameTextView.setText(exercises[i]);
        return v;
    }
}
