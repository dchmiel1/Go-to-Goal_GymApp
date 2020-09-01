package com.example.gotogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

public class WorkoutSetAdapter extends BaseAdapter {

    Vector<String> reps;
    Vector<String> kgs;
    LayoutInflater inflater;

    static class ViewHolder{
        TextView wTv;
        TextView rTv;
    }

    public WorkoutSetAdapter(Context c, Vector<String> reps, Vector<String> kgs){
        this.reps = reps;
        this.kgs = kgs;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return reps.size();
    }

    @Override
    public Object getItem(int i) {
        return reps.elementAt(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if(view == null){
            view = inflater.inflate(R.layout.exercise_in_workout_listview, null);
            holder = new ViewHolder();
            holder.rTv = view.findViewById(R.id.repsTextView);
            holder.wTv = view.findViewById(R.id.kgsTextView);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.rTv.setText(BodyWeightActivity.getProperVal(reps.elementAt(i)) + " reps");
        holder.wTv.setText(BodyWeightActivity.getProperVal(kgs.elementAt(i)) + " kg");
        return view;
    }
}
