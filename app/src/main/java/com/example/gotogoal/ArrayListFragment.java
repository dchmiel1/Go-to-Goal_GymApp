package com.example.gotogoal;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.ListFragment;
import java.util.Vector;

public class ArrayListFragment extends ListFragment {
    private long datetime;
    static Context c;
    private TextView emptyTextView;

    static ArrayListFragment init(long datetime, Context c) {
        ArrayListFragment.c = c;
        ArrayListFragment argsList = new ArrayListFragment();
        Bundle args = new Bundle();
        args.putLong("datetime", datetime);
        argsList.setArguments(args);
        return argsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datetime = getArguments() != null ? getArguments().getLong("datetime") : 1;
    }

    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_pager_list, container, false);
        emptyTextView = layoutView.findViewById(R.id.emptyTextView);
        emptyTextView.setVisibility(View.INVISIBLE);
        return layoutView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updatePage();
    }

    public void updatePage() {
        Cursor c = MainActivity.dbHelper.getSetsByDate(datetime);
        Vector<MainActivity.Training> trainings = new Vector<>();
        boolean found = false;
        MainActivity.Training training;
        while (c.moveToNext()) {
            for (int i = 0; i < trainings.size(); i++)
                if (trainings.elementAt(i).exercise.equals(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE)))) {
                    trainings.elementAt(i).reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
                    trainings.elementAt(i).kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
                    found = true;
                }
            if (found) {
                found = false;
                continue;
            }
            training = new MainActivity.Training();
            training.exercise = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_EXERCISE));
            training.reps = new Vector<>();
            training.kgs = new Vector<>();
            training.reps.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
            training.kgs.add(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
            trainings.add(training);
        }

        if(trainings.size() == 0)
            emptyTextView.setVisibility(View.VISIBLE);
        else
            emptyTextView.setVisibility(View.INVISIBLE);

        WorkoutAdapter workoutAdapter = new WorkoutAdapter(ArrayListFragment.c, trainings, this);
        setListAdapter(workoutAdapter);
    }
}