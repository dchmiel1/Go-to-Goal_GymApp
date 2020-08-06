package com.example.gotogoal;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MuscleAdapter extends BaseAdapter {

    LayoutInflater inflater;
    String muscles[];
    Image images[];

    public MuscleAdapter(Context c, String[] muscles){
        this.muscles = muscles;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return muscles.length;
    }

    @Override
    public Object getItem(int i) {
        return muscles[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.muscle_gridview, null);
        TextView muscleTextView = (TextView) v.findViewById(R.id.textView);
        muscleTextView.setText(muscles[i]);
        return v;
    }
}
