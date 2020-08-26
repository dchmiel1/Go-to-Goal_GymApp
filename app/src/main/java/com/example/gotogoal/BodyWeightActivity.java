package com.example.gotogoal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BodyWeightActivity extends AppCompatActivity {

    enum WhichPicker{ WEIGHT, HEIGHT, SEX, BIRTHDAY};

    private WhichPicker whichPicker;
    private DbHelper dbHelper;
    private SimpleDateFormat dateFormat;
    private BottomNavigationView navigationView;
    private String[] afterDecimal;
    private String[] sexes;
    private String[] heights;
    private int[] years;

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

    //bodyWeight items
    private GraphView weightGraph;
    private ImageButton addWeightImageButton;
    private NumberPicker weightPicker1;
    private NumberPicker weightPicker2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_weight);

        profileListView = (ListView) findViewById(R.id.profileListView);
        weightGraph = (GraphView) findViewById(R.id.weightGraph);
        navigationView = (BottomNavigationView) findViewById(R.id.profileNavigationBar);
        addWeightImageButton = (ImageButton) findViewById(R.id.addWeightImageButton);
        weightPicker1 = (NumberPicker) findViewById(R.id.bodyWeightPicker1);
        weightPicker2 = (NumberPicker) findViewById(R.id.bodyWeightPicker2);
        sexPicker = (NumberPicker) findViewById(R.id.sexPicker);
        heightPicker = (NumberPicker) findViewById(R.id.heightPicker);
        datePicker1 = (NumberPicker) findViewById(R.id.datePicker1);
        datePicker2 = (NumberPicker) findViewById(R.id.datePicker2);
        datePicker3 = (NumberPicker) findViewById(R.id.datePicker3);
        pickersLayout = (RelativeLayout) findViewById(R.id.pickersLayout);
        darkView = (View) findViewById(R.id.darkView);

        whichPicker = null;
        darkView.setVisibility(View.GONE);
        pickersLayout.setVisibility(View.GONE);
        profileListView.setVisibility(View.GONE);
        dbHelper = MainActivity.dbHelper;
        setAdapter();
        profileListView.setAdapter(profileListViewAdapter);
        dateFormat = new SimpleDateFormat("dd MMM");

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
                for(int i = 0; i < navigationView.getMenu().size(); i++)
                    navigationView.getMenu().getItem(i).setEnabled(true);
                switch(whichPicker){
                    case HEIGHT:
                        heightPicker.setVisibility(View.GONE);
                        int newHeight = Integer.parseInt(heights[heightPicker.getValue()].substring(0, heights[heightPicker.getValue()].length()-3));
                        System.out.println(newHeight);
                        break;
                    case WEIGHT:
                        weightPicker1.setVisibility(View.GONE);
                        weightPicker2.setVisibility(View.GONE);
                        String newWeight = getProperVal(weightPicker1.getValue() +
                                String.valueOf(afterDecimal[weightPicker2.getValue()]).substring(0, String.valueOf(afterDecimal[weightPicker2.getValue()]).length()-2));
                        dbHelper.insertWeight(Double.parseDouble(newWeight));
                        weightGraph.removeAllSeries();
                        weightGraph.addSeries(getGraphPoints());
                        weightGraph.addSeries(getGraphLines());
                        break;
                    case SEX:
                        sexPicker.setVisibility(View.GONE);
                        String newSex = sexes[sexPicker.getValue()];
                        System.out.println(newSex);
                        break;
                    default:
                        break;
                }
            }
        });

        profileListView.setOnItemClickListener((adapterView, view, i, l) -> {
            pickersLayout.setVisibility(View.VISIBLE);
            darkView.setVisibility(View.VISIBLE);
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
                    datePicker1.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams((int)(500), 200);
                    ConstraintLayout.LayoutParams lol= new ConstraintLayout.LayoutParams(1000, pickersLayout.getHeight());
                    pickersLayout.setLayoutParams(lol);
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
        PointsGraphSeries<DataPoint> pointPointsGraphSeries = new PointsGraphSeries<>(dataPoints);
        pointPointsGraphSeries.setColor(Color.parseColor("#FFFF8800"));
        return pointPointsGraphSeries;
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
        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>(dataPoints);
        lineGraphSeries.setColor(Color.parseColor("#FFFF8800"));
        return lineGraphSeries;
    }

    private void showBodyWeight(){
        profileListView.setVisibility(View.GONE);
        weightGraph.setVisibility(View.VISIBLE);
        addWeightImageButton.setVisibility(View.VISIBLE);
    }

    private void showProfile(){
        profileListView.setVisibility(View.VISIBLE);
        weightGraph.setVisibility(View.GONE);
        addWeightImageButton.setVisibility(View.GONE);
    }

    private void setAdapter(){
        String[] titles = getResources().getStringArray(R.array.profile_list_view_titles);
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
        }
        profileListViewAdapter = new ProfileListViewAdapter(this, titles, answers);
    }

    private void setPickers(){
        afterDecimal = new String[10];
        sexes = new String[] {"male", "female"};
        //years = new String[100];
        heights = new String[150];
        for(int i = 0; i < afterDecimal.length; i ++)
            afterDecimal[i] = String.valueOf(Math.round((i) * 0.1 * 100) / 100.0).substring(1) + " kg";
        for(int i = 0; i < heights.length; i ++)
            heights[i] = i+100 + " cm";
        //for(int i = 0; i < years.length; i ++)
            //years[i] = i + 1920;
        sexPicker.setDisplayedValues(sexes);
        weightPicker2.setDisplayedValues(afterDecimal);
        heightPicker.setDisplayedValues(heights);
        //datePicker3.setDisplayedValues(years);
    }
}