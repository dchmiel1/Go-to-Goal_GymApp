package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphsActivity extends AppCompatActivity {

    private Spinner exercisesSpinner;
    private Spinner musclesSpinner;
    private GraphView exerciseGraphView;
    private Context c;
    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private TextView notEnoughDataPointsGraphs;
    private TextView dataPointDetailsGraphs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        exerciseGraphView = (GraphView) findViewById(R.id.exerciseGraph);
        exercisesSpinner = (Spinner) findViewById(R.id.exercisesSpinner);
        musclesSpinner = (Spinner) findViewById(R.id.musclesSpinner);
        musclesSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.muscles)));
        notEnoughDataPointsGraphs = (TextView) findViewById(R.id.notEnoughDataPointsTextViewGraphs);
        dataPointDetailsGraphs = (TextView) findViewById(R.id.dataPointDetailsGraphs);
        c = this;
        dbHelper = MainActivity.dbHelper;
        dateFormat = new SimpleDateFormat("dd.MM");

        exerciseGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if(isValueX){
                    return dateFormat.format(new Date((long) value));
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });

        musclesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(musclesSpinner.getItemAtPosition(i).toString()){
                    case "Chest":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.chest_ex)));
                        break;
                    case "Back":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.back_ex)));
                        break;
                    case "Triceps":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.triceps_ex)));
                        break;
                    case "Biceps":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.biceps_ex)));
                        break;
                    case "Shoulders":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.shoulders_ex)));
                        break;
                    case "Legs":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.legs_ex)));
                        break;
                    default:
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.abs_ex)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        exercisesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setGraphSeries(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void setGraphSeries(int i){
        Cursor c = dbHelper.getExerciseOneReps(exercisesSpinner.getItemAtPosition(i).toString());
        exerciseGraphView.removeAllSeries();
        if(c.getCount() > 1){
            DataPoint[] dataPoints = new DataPoint[c.getCount()];
            SimpleDateFormat formatOfDateInSql = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat pointFormat = new SimpleDateFormat("dd MMM");
            int j = 0;
            while (c.moveToNext()) {
                try {
                    dataPoints[j] = new DataPoint(formatOfDateInSql.parse(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE))), c.getDouble(c.getColumnIndexOrThrow("max(one_rep)")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ++j;
            }
            PointsGraphSeries<DataPoint> pointsGraphSeries = new PointsGraphSeries<>(dataPoints);
            LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPoints);
            pointsGraphSeries.setColor(Color.parseColor("#FFFF8800"));
            lineGraphSeries.setColor(Color.parseColor("#FFFF8800"));
            notEnoughDataPointsGraphs.setVisibility(View.GONE);
            dataPointDetailsGraphs.setVisibility(View.VISIBLE);
            exerciseGraphView.addSeries(lineGraphSeries);
            exerciseGraphView.addSeries(pointsGraphSeries);
            exerciseGraphView.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length);
            pointsGraphSeries.setOnDataPointTapListener( (series, dataPoint) -> dataPointDetailsGraphs.setText(BodyWeightActivity.getProperVal(String.valueOf(dataPoint.getY())) + ", " + pointFormat.format(new Date((long)dataPoint.getX()))));
        }else {
            notEnoughDataPointsGraphs.setVisibility(View.VISIBLE);
            dataPointDetailsGraphs.setVisibility(View.VISIBLE);
        }
    }
}