package com.example.gotogoal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static Date date;
    public static DbHelper dbHelper;
    public static int[] multiplier;
    public static SimpleDateFormat dateFormatInDb;
    private TextView dateTextView;
    private ViewPager viewPager;
    static public Animation slideLeftIn;
    static int START_POS = 5000;
    static int curr_pos;

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private Context c;

        public ViewPagerAdapter(FragmentManager fragmentManager, Context c) {
            super(fragmentManager);
            this.c = c;
        }
        @Override
        public int getCount() {
            return 10000;
        }

        @Override
        public Fragment getItem(int position) {
            long datetime = date.getTime();

            if(position == curr_pos + 1)
                datetime = date.getTime() + 1000 * 60 * 60 * 24;
            else if (position == curr_pos - 1)
                datetime = date.getTime() - 1000 * 60 * 60 * 24;

            return ArrayListFragment.init(datetime, c);
        }
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(R.style.AppThemeWithoutBar, true);
        return theme;
    }

    @Override
    public void onBackPressed() {
        if(dateTextView.getText() == "Today") {
            this.finishAffinity();
        }
        else {
            curr_pos = START_POS;
            updateDate(System.currentTimeMillis());
            viewPager.setCurrentItem(curr_pos, true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormatInDb = new SimpleDateFormat("yyyy.MM.dd");
        dateTextView = findViewById(R.id.dateTextView);
        multiplier = getResources().getIntArray(R.array.multipliers);
        ImageView profileImageView = findViewById(R.id.profileImageView);
        ImageView addImageView = findViewById(R.id.addImageView);
        ImageView achievementsImageView = findViewById(R.id.achievementsImageView);
        ImageView graphsImageView = findViewById(R.id.graphsImageView);
        ImageView rightImageBtn = findViewById(R.id.rightImageBtn);
        ImageView leftImageBtn = findViewById(R.id.leftImageBtn);
        viewPager = findViewById(R.id.viewpager);
        slideLeftIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_in);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageMargin(40);
        viewPager.setPageMarginDrawable(R.color.colorBrightMenu);

        if(getIntent().hasExtra("date")) {
            try {
                date = dateFormatInDb.parse(getIntent().getExtras().getString("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            curr_pos = START_POS - (int) ((System.currentTimeMillis() - date.getTime()) / TimeUnit.DAYS.toMillis(1));
            updateDate(date.getTime());
        }else {
            curr_pos = START_POS;
            updateDate(System.currentTimeMillis());
        }

        viewPager.setCurrentItem(curr_pos);

        if(dbHelper == null)
            dbHelper = new DbHelper(this, this);

        graphsImageView.setOnClickListener(view -> {
            Intent showGraphsActivityIntent = new Intent(getApplicationContext(), GraphsActivity.class);
            startActivity(showGraphsActivityIntent);
        });

        achievementsImageView.setOnClickListener(view -> {
            Intent showAchievementsIntent = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(showAchievementsIntent);
        });

        profileImageView.setOnClickListener(view -> {
            Intent showProfileIntent = new Intent(getApplicationContext(), BodyWeightActivity.class);
            startActivity(showProfileIntent);
        });

        addImageView.setOnClickListener(view -> {
            Intent showMusclesIntent = new Intent(getApplicationContext(), MusclesActivity.class);
            startActivity(showMusclesIntent);
        });

        rightImageBtn.setOnClickListener(view -> {
            nextDay();
            viewPager.setCurrentItem(curr_pos, true);
        });

        leftImageBtn.setOnClickListener(view -> {
            previousDay();
            viewPager.setCurrentItem(curr_pos, true);
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if(position > curr_pos)
                    nextDay();
                else if(position < curr_pos)
                    previousDay();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void updateDate(long datetime){
        if(date == null)
            date = new Date(datetime);
        date.setTime(datetime);
        String dateText = "";
        if(DateUtils.isToday(datetime))
            dateText = "Today";
        if(DateUtils.isToday(datetime + TimeUnit.DAYS.toMillis(1)))
            dateText = "Yesterday";
        if(DateUtils.isToday(datetime - TimeUnit.DAYS.toMillis(1)))
            dateText = "Tomorrow";
        if(dateText == "")
            dateText = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date);

        dateTextView.setText(dateText);
    }

    public static class Training {
        public String exercise;
        public Vector<String> reps;
        public Vector<String> kgs;
    }

    private void nextDay() {
        curr_pos += 1;
        updateDate(date.getTime() + (1000 * 60 * 60 * 24));
    }

    private void previousDay() {
        curr_pos -= 1;
        updateDate(date.getTime() - (1000 * 60 * 60 * 24));
    }
}