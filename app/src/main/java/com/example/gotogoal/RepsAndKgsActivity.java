package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RepsAndKgsActivity extends AppCompatActivity {

    private int numOfSets = 1;
    private LayoutInflater inflater;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_and_kgs);

        final TextView exNameTextView = ((TextView) findViewById(R.id.exNameTextView));
        final String exName = getIntent().getExtras().getString("ex_name");
        exNameTextView.setText(exName);
        Button saveBtn = (Button) findViewById(R.id.saveButton);
        dbHelper = MainActivity.dbHelper;

        final ListView setsListView = (ListView) findViewById(R.id.setsListView);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setsListView.setAdapter(new ArrayAdapter<String>(this, R.layout.set_listview, new String[0]));
        setsListView.addFooterView(newListItem(-1));

        setsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setVisiblity(view);
                    setsListView.addFooterView(newListItem(i));
                    ++numOfSets;
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                for(int i = 0; i < numOfSets; i ++){
                    View v = setsListView.getChildAt(i);
                    String reps = ((EditText) v.findViewById(R.id.repsEditText)).getText().toString();
                    String kgs = ((EditText) v.findViewById(R.id.kgsEditText)).getText().toString();
                    if(!reps.equals("") && !kgs.equals("")){
                        dbHelper.insertSet(exName, Integer.parseInt(reps), Double.parseDouble(kgs));
                    }
                }
                startActivity(mainActivityIntent);
            }
        });
    }

    private View newListItem(int i){
        View v = inflater.inflate(R.layout.set_listview, null);
        TextView numOfSetTextView = (TextView) v.findViewById(R.id.numOfSetTextView);
        EditText repsEditText = (EditText) v.findViewById(R.id.repsEditText);
        EditText kgsEditText = (EditText) v.findViewById(R.id.kgsEditText);
        TextView repsTextView = (TextView) v.findViewById(R.id.repsTextView);
        TextView kgsTextView = (TextView) v.findViewById(R.id.kgsTextView);

        repsEditText.setVisibility(View.GONE);
        kgsEditText.setVisibility(View.GONE);
        kgsTextView.setVisibility(View.GONE);
        repsTextView.setVisibility(View.GONE);
        numOfSetTextView.setText(i+2+"");
        return v;
    }

    private void setVisiblity(View v){
        EditText repsEditText = (EditText) v.findViewById(R.id.repsEditText);
        EditText kgsEditText = (EditText) v.findViewById(R.id.kgsEditText);
        TextView plusTextView = (TextView) v.findViewById(R.id.plus);
        TextView repsTextView = (TextView) v.findViewById(R.id.repsTextView);
        TextView kgsTextView = (TextView) v.findViewById(R.id.kgsTextView);

        repsEditText.setVisibility(View.VISIBLE);
        kgsEditText.setVisibility(View.VISIBLE);
        kgsTextView.setVisibility(View.VISIBLE);
        repsTextView.setVisibility(View.VISIBLE);
        plusTextView.setVisibility(View.GONE);
    }
}