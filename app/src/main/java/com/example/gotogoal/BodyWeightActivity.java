package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BodyWeightActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_weight);

        dbHelper = MainActivity.dbHelper;
        dateFormat = new SimpleDateFormat("dd MMM");
        final TextView weightTextView = (TextView) findViewById(R.id.weightTextView);
        final TextView heightTextView = (TextView) findViewById(R.id.heightTextView);
        final EditText heightEditText = (EditText) findViewById(R.id.heightEditText);
        final EditText weightEditText = (EditText) findViewById(R.id.weightEditText);
        final Button changeValBtn = (Button) findViewById(R.id.changeValBtn);
        final Button saveBtn = (Button) findViewById(R.id.saveBtn);
        final TextView bmiTextView = (TextView) findViewById(R.id.bmiTextView);
        final GraphView weightGraph = (GraphView) findViewById(R.id.weightGraph);

        weightGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX){
                    return dateFormat.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });
        weightGraph.addSeries(getGraphPoints());
        weightGraph.addSeries(getGraphLines());



        Cursor c = dbHelper.getLastWeight();
        if(c.getCount()> 0) {
            c.moveToNext();
            String weight = getProperVal(String.valueOf(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))));
            String height = getProperVal(String.valueOf(c.getInt(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS))));
            weightEditText.setText(weight);
            heightEditText.setText(height);
            weightTextView.setText(weight);
            heightTextView.setText(height);
            bmiTextView.setText(getProperVal(String.valueOf(Double.parseDouble(weight)/(Double.parseDouble(height)*Double.parseDouble(height)/10000))));
        }

        changeValBtn.setOnClickListener(view -> {
            changeValBtn.setVisibility(View.GONE);
            weightEditText.setVisibility(View.VISIBLE);
            heightEditText.setVisibility(View.VISIBLE);
            weightTextView.setVisibility(View.GONE);
            heightTextView.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
        });

        saveBtn.setOnClickListener(view -> {
            if(!heightEditText.getText().toString().equals("") && !weightEditText.getText().toString().equals("")) {
                String newWeight = getProperVal(weightEditText.getText().toString());
                String newHeight = getProperVal(heightEditText.getText().toString());
                dbHelper.insertOrUpdateWeight(Double.parseDouble(newWeight), Integer.parseInt(newHeight));
                String bmi = Double.parseDouble(newWeight)/(Double.parseDouble(newHeight)*Double.parseDouble(newHeight)/10000) + "";
                weightTextView.setText(newWeight);
                heightTextView.setText(newHeight);
                weightEditText.setText(newWeight);
                heightEditText.setText(newHeight);
                bmiTextView.setText(getProperVal(bmi));
                changeValBtn.setVisibility(View.VISIBLE);
                weightEditText.setVisibility(View.GONE);
                heightEditText.setVisibility(View.GONE);
                weightTextView.setVisibility(View.VISIBLE);
                heightTextView.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.GONE);
                weightGraph.removeAllSeries();
                weightGraph.addSeries(getGraphPoints());
                weightGraph.addSeries(getGraphLines());
            }
        });
    }

    public static String getProperVal(String val){
        String sVal = Double.parseDouble(val)+"";
        for(int i = 0; i < sVal.length(); i++){
            if(sVal.charAt(i) =='.') {
                if (sVal.charAt(i + 1) == '0' && sVal.length() >= i + 2) {
                    return sVal.substring(0, i);
                } else {
                    if (sVal.length() > i + 2) {
                        return sVal.substring(0, i + 2);
                    }
                }
            }
        }
        return sVal;
    }

    private PointsGraphSeries<DataPoint> getGraphPoints(){
        Cursor c = dbHelper.getWeightData();
        DataPoint[] dataPoints = new DataPoint[c.getCount()];
        SimpleDateFormat formatOfDateInSql = new SimpleDateFormat("yyyy MM dd");

        int i = 0;
        while(c.moveToNext()){
            try {
                dataPoints[i] = new DataPoint(formatOfDateInSql.parse(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE))), c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ++i;
        }
        return new PointsGraphSeries<DataPoint>(dataPoints);
    }

    private LineGraphSeries<DataPoint> getGraphLines(){
        Cursor c = dbHelper.getWeightData();
        DataPoint[] dataPoints = new DataPoint[c.getCount()];
        SimpleDateFormat formatOfDateInSql = new SimpleDateFormat("yyyy MM dd");

        int i = 0;
        while(c.moveToNext()){
            try {
                dataPoints[i] = new DataPoint(formatOfDateInSql.parse(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE))), c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ++i;
        }
        return new LineGraphSeries<DataPoint>(dataPoints);
    }
}