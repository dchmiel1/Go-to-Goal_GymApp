package com.example.gotogoal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
    private ArrayListFragment listFragment;

    static class ViewHolder{
        ListView lV;
        TextView exTv;
        ImageView dIv;
        ImageView chIv;
        View cV;
        View divider;
    }

    public WorkoutAdapter(Context c, Vector<MainActivity.Training> trainings, ArrayListFragment listFragment){
        this.trainings = trainings;
        this.c = c;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbHelper = MainActivity.dbHelper;
        this.listFragment = listFragment;
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
        ViewHolder holder;

        if(view == null){
            view = inflater.inflate(R.layout.workout_listview, null);
            holder = new ViewHolder();
            holder.dIv = view.findViewById(R.id.deleteImageView);
            holder.exTv = view.findViewById(R.id.exNameTextView);
            holder.lV = view.findViewById(R.id.setsListView);
            holder.cV = view.findViewById(R.id.clickView);
            holder.chIv = view.findViewById(R.id.exerciseChartImageView);
            holder.divider = view.findViewById(R.id.divider);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.exTv.setText(trainings.elementAt(i).exercise);

        if(c.getClass() == MainActivity.class) {
            holder.cV.setOnClickListener(view1 -> updateSets(i));
            View finalView = view;
            holder.cV.setOnLongClickListener(view1 -> showDelete(finalView, i));
        }else{
            view.setBackground(null);
            holder.divider.setVisibility(VISIBLE);
        }

        WorkoutSetAdapter workoutSetAdapter = new WorkoutSetAdapter(c, trainings.elementAt(i).reps, trainings.elementAt(i).kgs);
        holder.lV.setAdapter(workoutSetAdapter);

        holder.dIv.setOnClickListener(view1 -> {
            dbHelper.deleteByDateAndExercise(trainings.elementAt(i).exercise);
            listFragment.updatePage();
        });

        holder.chIv.setOnClickListener(view1 -> {
            Intent showChartIntent = new Intent(c, GraphsActivity.class);
            showChartIntent.putExtra("exercise", trainings.elementAt(i).exercise);
            c.startActivity(showChartIntent);
            listFragment.updatePage();
        });

        ConstraintLayout.LayoutParams mParam = new ConstraintLayout.LayoutParams(-1, (int)(holder.exTv.getTextSize()*1.9) + (int)(holder.exTv.getTextSize() *(1.63* trainings.elementAt(i).reps.size())));
        view.setLayoutParams(mParam);
        return view;
    }

    public void updateSets(int i) {
        Intent showRepsAndKgsActivity = new Intent(c, WorkoutTrackActivity.class);
        showRepsAndKgsActivity.putExtra("ex_name", trainings.elementAt(i).exercise);
        c.startActivity(showRepsAndKgsActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean showDelete(View v, int i){
        v.findViewById(R.id.deleteImageView).setVisibility(VISIBLE);
        v.findViewById(R.id.deleteImageView).startAnimation(MainActivity.slideLeftIn);
        v.findViewById(R.id.exerciseChartImageView).setVisibility(VISIBLE);
        v.findViewById(R.id.exerciseChartImageView).startAnimation(MainActivity.slideLeftIn);
        v.findViewById(R.id.clickView).setOnClickListener(view -> hideDelete(v, i));
        v.setBackgroundColor(Color.parseColor("#696360"));
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideDelete(View v, int i){
        v.findViewById(R.id.deleteImageView).setVisibility(View.GONE);
        v.findViewById(R.id.exerciseChartImageView).setVisibility(View.GONE);
        v.findViewById(R.id.clickView).setOnClickListener(view -> updateSets(i));
        v.setBackground(c.getResources().getDrawable(R.drawable.border_dark_orange, null));
    }

}
