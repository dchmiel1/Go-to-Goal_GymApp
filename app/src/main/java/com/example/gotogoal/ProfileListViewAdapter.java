package com.example.gotogoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ProfileListViewAdapter extends BaseAdapter {

    private String[] titles;
    private String[] answers;
    private LayoutInflater inflater;
    private String[] units;

    public ProfileListViewAdapter(Context c, String[] titles, String[] answers, String[] units){
        this.titles = titles;
        this.answers = answers;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.units = units;
    }
    @Override
    public int getCount() {
        return titles.length;
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
        View v = inflater.inflate(R.layout.profile_list_view, null);

        ((TextView) v.findViewById(R.id.titleTextView)).setText(titles[i]);
        ((TextView) v.findViewById(R.id.answerTextView)).setText(answers[i] + units[i]);

        return v;
    }
}
