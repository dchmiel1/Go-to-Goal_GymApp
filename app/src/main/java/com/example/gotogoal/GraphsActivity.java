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
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphsActivity extends AppCompatActivity {

    private Spinner exercisesSpinner;
    private Spinner musclesSpinner;
    private Context c;
    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private TextView dataPointDetailsGraphs;
    private LineChart exerciseChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        exercisesSpinner = findViewById(R.id.exercisesSpinner);
        musclesSpinner = findViewById(R.id.musclesSpinner);
        musclesSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.muscles)));
        dataPointDetailsGraphs = findViewById(R.id.dataPointDetailsGraphs);
        exerciseChart = findViewById(R.id.exerciseChart);
        exerciseChart.setNoDataText("Add at least 2 workouts to see the chart");
        exerciseChart.setNoDataTextColor(Color.parseColor("#8a000000"));
        exerciseChart.getPaint(Chart.PAINT_INFO).setTextSize(Utils.convertDpToPixel(17f));
        c = this;
        dbHelper = MainActivity.dbHelper;
        dateFormat = new SimpleDateFormat("dd.MM");

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
                    case "Thighs":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.thighs_ex)));
                        break;
                    case "ABS":
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.abs_ex)));
                        break;
                    default:
                        exercisesSpinner.setAdapter(new ArrayAdapter<>(c, R.layout.exercise_spinner_view, getResources().getStringArray(R.array.calves_ex)));
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
        if(c.getCount() > 1){
            ArrayList<Entry> values = new ArrayList<>();
            SimpleDateFormat pointFormat = new SimpleDateFormat("dd MMM");
            while (c.moveToNext()) {
                try {
                    values.add(new Entry((float) new Long(MainActivity.dateFormatInDb.parse(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE))).getTime()) + (1000 * 60 * 60 * 12), new Double(c.getDouble(c.getColumnIndexOrThrow("max(one_rep)"))).floatValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            XAxis xAxis = exerciseChart.getXAxis();
            xAxis.setValueFormatter(new XAxisValueFormatter());
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            LineDataSet dataSet = new LineDataSet(values, "One rep max");
            dataSet.setColor(Color.parseColor("#FFFF8800"));
            dataSet.setCircleColor(Color.parseColor("#FFFF8800"));
            dataSet.setLineWidth(2);
            dataSet.setCircleSize(4);
            dataSet.setValueTextSize(9);
            LineData lineData = new LineData(dataSet);
            exerciseChart.setData(lineData);
            exerciseChart.invalidate();
            exerciseChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    Cursor c2 = dbHelper.getSetByDateAndOneRep(MainActivity.dateFormatInDb.format(new Date((long) (e.getX()))), e.getY(), exercisesSpinner.getItemAtPosition(i).toString());
                    if(c2.getCount() > 0) {
                        c2.moveToNext();
                        dataPointDetailsGraphs.setText(BodyWeightActivity.getProperVal(String.valueOf(e.getY())) + " kg " +
                                " (" + BodyWeightActivity.getProperVal(String.valueOf(c2.getDouble(c2.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED)))) +
                                " kg x " + c2.getInt(c2.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS)) + "), " +
                                pointFormat.format(new Date((long) e.getX())));
                        dataPointDetailsGraphs.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected() {
                    dataPointDetailsGraphs.setVisibility(View.GONE);
                }
            });

        }
        else{
            exerciseChart.clear();
            dataPointDetailsGraphs.setText("");
            dataPointDetailsGraphs.setVisibility(View.GONE);
        }
    }

    private class XAxisValueFormatter extends ValueFormatter {

        public XAxisValueFormatter(){
        }

        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }
}