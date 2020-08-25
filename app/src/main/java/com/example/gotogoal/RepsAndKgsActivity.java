package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RepsAndKgsActivity extends AppCompatActivity{

    private int newSets = 1;
    private LayoutInflater inflater;
    private DbHelper dbHelper;
    private String exName;
    private ListView setsListView;
    private int[] idsToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_and_kgs);

        Button saveBtn = (Button) findViewById(R.id.saveButton);
        final TextView exNameTextView = ((TextView) findViewById(R.id.exNameTextView));

        setsListView = (ListView) findViewById(R.id.setsListView);
        exName = getIntent().getExtras().getString("ex_name");
        exNameTextView.setText(exName);
        dbHelper = MainActivity.dbHelper;
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setsListView.setAdapter(new ArrayAdapter<String>(this, R.layout.set_listview, new String[0]));

        idsToUpdate = display();

        setsListView.setOnItemClickListener((adapterView, view, i, l) -> {
                setVisiblity(view);
                setsListView.addFooterView(getNewSetView(i));
                ++newSets;
        });

        saveBtn.setOnClickListener(view -> {
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

            for(int i = 0; i < idsToUpdate.length; i ++){
                View v = setsListView.getChildAt(i);
                String reps = ((EditText) v.findViewById(R.id.repsEditText)).getText().toString();
                String kgs = ((EditText) v.findViewById(R.id.kgsEditText)).getText().toString();
                if(!reps.equals("") && !kgs.equals("")){
                    dbHelper.updateSet(idsToUpdate[i], Integer.parseInt(reps), Double.parseDouble(kgs), exName);
                }
                else{
                    dbHelper.deleteById(idsToUpdate[i]);
                }
            }
            for(int i = idsToUpdate.length; i < idsToUpdate.length + newSets; i ++){
                View v = setsListView.getChildAt(i);
                String reps = ((EditText) v.findViewById(R.id.repsEditText)).getText().toString();
                String kgs = ((EditText) v.findViewById(R.id.kgsEditText)).getText().toString();
                if(!reps.equals("") && !kgs.equals("")){
                    dbHelper.insertSet(exName, Integer.parseInt(reps), Double.parseDouble(kgs));
                }
            }
            startActivity(mainActivityIntent);
        });
    }

    private View getNewSetView(int i){
        View v = inflater.inflate(R.layout.set_listview, null);
        ((EditText) v.findViewById(R.id.repsEditText)).setVisibility(View.GONE);
        ((EditText) v.findViewById(R.id.kgsEditText)).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.repsTextView)).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.kgsTextView)).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.numOfSetTextView)).setText(i+2+"");
        return v;
    }

    private void setVisiblity(View v){
        ((EditText) v.findViewById(R.id.repsEditText)).setVisibility(View.VISIBLE);
        ((EditText) v.findViewById(R.id.kgsEditText)).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.plus)).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.repsTextView)).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.kgsTextView)).setVisibility(View.VISIBLE);
    }

    private View getSetView(int i, int reps, double kgs){
        View v = inflater.inflate(R.layout.set_listview, null);
        ((TextView) v.findViewById(R.id.numOfSetTextView)).setText(i+"");
        ((EditText) v.findViewById(R.id.repsEditText)).setText(reps + "");
        ((EditText) v.findViewById(R.id.kgsEditText)).setText(ProfileActivity.getProperVal(String.valueOf(kgs)));
        ((TextView) v.findViewById(R.id.plus)).setVisibility(View.GONE);
        return v;
    }

    private int[] display(){
        int i = 0;
        int[] ids;
        Cursor c = dbHelper.getSetsByDateAndExercise(new SimpleDateFormat("yyyy MM dd", Locale.getDefault()).format(MainActivity.date), exName);
        ids = new int[c.getCount()];
        while(c.moveToNext()){
            ids[i] = c.getInt(c.getColumnIndexOrThrow(DbNames._ID));
            ++i;
            setsListView.addFooterView(getSetView(i, c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)), c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
        }
        setsListView.addFooterView(getNewSetView(i-1));
        return ids;
    }
}