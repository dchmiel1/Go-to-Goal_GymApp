package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shawnlin.numberpicker.NumberPicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class BodyWeightActivity extends AppCompatActivity {

    enum WhichPicker{ WEIGHT, HEIGHT, SEX, BIRTHDAY}

    private WhichPicker whichPicker;
    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private BottomNavigationView navigationView;
    private String[] afterDecimal;
    private String[] heights;
    private String[] months;
    private String[] days;
    private boolean graphDataVisible;

    //profile items
    private ListView profileListView;
    private ProfileListViewAdapter profileListViewAdapter;
    private RelativeLayout pickersLayout;
    private View darkView;
    private NumberPicker sexPicker;
    private NumberPicker heightPicker;
    private NumberPicker datePicker1;
    private NumberPicker datePicker2;
    private NumberPicker datePicker3;
    private RelativeLayout datePickersLayout;

    //bodyWeight items
    private LineChart weightChart;
    private ImageView addWeightImageButton;
    private NumberPicker weightPicker1;
    private NumberPicker weightPicker2;
    private TextView dataPointDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_weight);

        profileListView = findViewById(R.id.profileListView);
        navigationView = findViewById(R.id.profileNavigationBar);
        addWeightImageButton = findViewById(R.id.addWeightImageButton);
        weightPicker1 = findViewById(R.id.bodyWeightPicker1);
        weightPicker2 = findViewById(R.id.bodyWeightPicker2);
        sexPicker = findViewById(R.id.sexPicker);
        heightPicker = findViewById(R.id.heightPicker);
        datePicker1 = findViewById(R.id.datePicker1);
        datePicker2 = findViewById(R.id.datePicker2);
        datePicker3 = findViewById(R.id.datePicker3);
        pickersLayout = findViewById(R.id.pickersLayout);
        darkView = findViewById(R.id.darkView);
        datePickersLayout = findViewById(R.id.datePickersLayout);
        dataPointDetails = findViewById(R.id.dataPointDetails);
        weightChart = findViewById(R.id.weightChart);

        weightChart.setNoDataText("Add at least 2 body weight data to see the chart");
        weightChart.setNoDataTextColor(Color.parseColor("#8a000000"));
        weightChart.getPaint(Chart.PAINT_INFO).setTextSize(Utils.convertDpToPixel(17f));
        whichPicker = null;
        datePickersLayout.setVisibility(View.GONE);
        darkView.setVisibility(View.GONE);
        pickersLayout.setVisibility(View.GONE);
        profileListView.setVisibility(View.GONE);
        dbHelper = MainActivity.dbHelper;
        setAdapter();
        profileListView.setAdapter(profileListViewAdapter);
        dateFormat = new SimpleDateFormat("dd.MM");

        setGraphSeries();
        setPickers();

        navigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.toString().equals("Body weight"))
                showBodyWeight();
            else
                showProfile();
            return true;
        });

        addWeightImageButton.setOnClickListener(view -> {
            if (darkView.getVisibility() == View.GONE) {
                pickersLayout.setVisibility(View.VISIBLE);
                darkView.setVisibility(View.VISIBLE);
                whichPicker = WhichPicker.WEIGHT;
                for(int i = 0; i < navigationView.getMenu().size(); i++)
                    navigationView.getMenu().getItem(i).setEnabled(false);
                weightPicker1.setVisibility(View.VISIBLE);
                weightPicker2.setVisibility(View.VISIBLE);
            }else{
                darkView.setVisibility(View.GONE);
                pickersLayout.setVisibility(View.GONE);
                addWeightImageButton.setImageResource(R.drawable.scale);
                for(int i = 0; i < navigationView.getMenu().size(); i++)
                    navigationView.getMenu().getItem(i).setEnabled(true);
                switch(whichPicker){
                    case HEIGHT:
                        heightPicker.setVisibility(View.GONE);
                        int newHeight = Integer.parseInt(heights[heightPicker.getValue()-1].substring(0, heights[heightPicker.getValue()].length()-3));
                        dbHelper.updateProfileInfo(0, null, newHeight, 0, whichPicker);
                        break;
                    case WEIGHT:
                        weightPicker1.setVisibility(View.GONE);
                        weightPicker2.setVisibility(View.GONE);
                        String newWeight = getProperVal(weightPicker1.getValue() +
                                String.valueOf(afterDecimal[weightPicker2.getValue()-1]).substring(0, String.valueOf(afterDecimal[weightPicker2.getValue()]).length()-2));
                        dbHelper.insertWeight(Double.parseDouble(newWeight));
                        dbHelper.updateProfileInfo(0, null, 0, Double.parseDouble(newWeight), whichPicker);
                        setGraphSeries();
                        break;
                    case SEX:
                        sexPicker.setVisibility(View.GONE);
                        int newSex = sexPicker.getValue();
                        dbHelper.updateProfileInfo(newSex, null, 0, 0, whichPicker);
                        break;
                    default:
                        datePickersLayout.setVisibility(View.GONE);
                        String newDayOfBirth = datePicker3.getValue() + "." + months[datePicker2.getValue()-1] + "." + days[datePicker1.getValue()-1];
                        dbHelper.updateProfileInfo(0, newDayOfBirth, 0, 0, whichPicker);
                        break;
                }
                if(navigationView.getSelectedItemId() == R.id.profile_info){
                    addWeightImageButton.setVisibility(View.GONE);
                }
            }
            setAdapter();
            profileListView.setAdapter(profileListViewAdapter);
        });

        profileListView.setOnItemClickListener((adapterView, view, i, l) -> {
            pickersLayout.setVisibility(View.VISIBLE);
            darkView.setVisibility(View.VISIBLE);
            addWeightImageButton.setImageResource(R.drawable.plus2);
            for(int j = 0; j < navigationView.getMenu().size(); j++)
                navigationView.getMenu().getItem(j).setEnabled(false);
            addWeightImageButton.setVisibility(View.VISIBLE);
            switch(i){
                case 0:
                    whichPicker = WhichPicker.SEX;
                    sexPicker.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    whichPicker = WhichPicker.BIRTHDAY;
                    pickersLayout.setVisibility(View.GONE);
                    datePickersLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    whichPicker = WhichPicker.HEIGHT;
                    heightPicker.setVisibility(View.VISIBLE);
                    break;
                default:
                    whichPicker = WhichPicker.WEIGHT;
                    weightPicker1.setVisibility(View.VISIBLE);
                    weightPicker2.setVisibility(View.VISIBLE);
                    break;
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

    private void setGraphSeries(){
        Cursor c = dbHelper.getWeightData();
        if(c.getCount() > 1){
            ArrayList<Entry> values = new ArrayList<>();
            SimpleDateFormat pointFormat = new SimpleDateFormat("dd MMM");
            while (c.moveToNext()) {
                try {
                    values.add(new Entry((float) new Long(MainActivity.dateFormatInDb.parse(c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE))).getTime()) + (1000 * 60 * 60 * 12), new Double(c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED))).floatValue()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            XAxis xAxis = weightChart.getXAxis();
            xAxis.setValueFormatter(new XAxisValueFormatter());
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            LineDataSet dataSet = new LineDataSet(values, "Body weight");
            dataSet.setColor(Color.parseColor("#FFFF8800"));
            dataSet.setCircleColor(Color.parseColor("#FFFF8800"));
            dataSet.setLineWidth(2);
            dataSet.setCircleSize(4);
            dataSet.setValueTextSize(9);
            LineData lineData = new LineData(dataSet);
            weightChart.setData(lineData);
            weightChart.invalidate();
            weightChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    dataPointDetails.setVisibility(View.VISIBLE);
                    dataPointDetails.setText(getProperVal(String.valueOf(e.getY())) + " kg, " + pointFormat.format(new Date((long) e.getX())));
                }

                @Override
                public void onNothingSelected() {
                    dataPointDetails.setVisibility(View.GONE);
                }
            });

        }
        else{
            graphDataVisible = false;
        }
    }

    private void showBodyWeight(){
        profileListView.setVisibility(View.GONE);
        weightChart.setVisibility(View.VISIBLE);
        addWeightImageButton.setVisibility(View.VISIBLE);
        if(graphDataVisible) {
            dataPointDetails.setVisibility(View.VISIBLE);
        }
        else {
            dataPointDetails.setVisibility(View.GONE);
        }
    }

    private void showProfile(){
        profileListView.setVisibility(View.VISIBLE);
        weightChart.setVisibility(View.GONE);
        addWeightImageButton.setVisibility(View.GONE);
        dataPointDetails.setVisibility(View.GONE);
    }

    private void setAdapter(){
        String[] titles = getResources().getStringArray(R.array.profile_list_view_titles);
        String[] units = getResources().getStringArray(R.array.units);
        String[] answers = new String[titles.length];
        Cursor c = dbHelper.getProfileInfo();
        if(c.getCount() > 0) {
            c.moveToNext();
            if (c.getDouble(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_ONE_REP)) == 0)
                answers[0] = "male";
            else
                answers[0] = "female";
            answers[1] = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_DATE));
            answers[2] = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_REPS));
            answers[3] = c.getString(c.getColumnIndexOrThrow(DbNames.COLUMN_NAME_KG_ADDED));
        }else{
            Arrays.fill(answers, "");
        }
        profileListViewAdapter = new ProfileListViewAdapter(this, titles, answers, units);
    }

    private void setPickers(){
        afterDecimal = new String[10];
        String[] sexes = new String[]{"male", "female"};
        String[] years = new String[100];
        heights = new String[150];
        days = new String[31];
        months = new String[12];
        for(int i = 0; i < afterDecimal.length; i ++)
            afterDecimal[i] = String.valueOf(Math.round((i) * 0.1 * 100) / 100.0).substring(1) + " kg";
        for(int i = 0; i < heights.length; i ++)
            heights[i] = i+100 + " cm";
        for(int i = 0; i < years.length; i ++)
            years[i] = String.valueOf(i + 1920);
        for(int i = 0; i < days.length; i++){
            if(i +1 < 10)
                days[i] = "0" + (i+1);
            else
                days[i] = i+1 + "";
        }
        for(int i = 0; i < months.length; i++){
            if(i +1 < 10)
                months[i] = "0" + (i+1);
            else
                months[i] = i+1 + "";
        }
        sexPicker.setDisplayedValues(sexes);
        weightPicker2.setDisplayedValues(afterDecimal);
        heightPicker.setDisplayedValues(heights);
        datePicker3.setDisplayedValues(years);
        datePicker2.setDisplayedValues(months);
        datePicker1.setDisplayedValues(days);
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