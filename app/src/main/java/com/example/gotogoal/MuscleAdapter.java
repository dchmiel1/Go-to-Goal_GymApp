package com.example.gotogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MuscleAdapter extends BaseAdapter {

    LayoutInflater inflater;
    String[] muscles;
    int[] images;

    public MuscleAdapter(Context c, String[] muscles){
        this.muscles = muscles;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        images = new int[muscles.length];
        images[0] = R.drawable.chest;
        images[1] = R.drawable.abs;
        images[2] = R.drawable.back;
        images[3] = R.drawable.shoulders;
        images[4] = R.drawable.biceps;
        images[5] = R.drawable.triceps;
        images[6] = R.drawable.legs;
        images[7] = R.drawable.calves;
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
        TextView muscleTextView = v.findViewById(R.id.muscleInGridViewTextView);
        ImageView muscleImageView = v.findViewById(R.id.imageView);
        muscleImageView.setImageResource(images[i]);
        muscleTextView.setText(muscles[i]);
        return v;
    }

}
