package com.l24o.syberiasoftprojecttest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;

import com.l24o.syberiasoftprojecttest.animation.DepthPageTransformer;
import com.l24o.syberiasoftprojecttest.animation.ZoomOutPageTransformer;
import com.l24o.syberiasoftprojecttest.model.Image;
import com.l24o.syberiasoftprojecttest.realm.RealmHelper;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    Timer timer;
    private ViewPager viewPager;
    private long count;
    private SharedPreferences preferences;

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        count = RealmHelper.getCount(Realm.getDefaultInstance(), !preferences.getBoolean("pref_all_or_favorites", false));
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
    }

    @Override
    protected void onResume() {
        super.onResume();
        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.images);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        List<Image> all = RealmHelper.getAll(Realm.getDefaultInstance(), Image.class);
        if (all.isEmpty())
            RealmHelper.save(Realm.getDefaultInstance(), Image.class, is);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state== ViewPager.SCROLL_STATE_IDLE) {
                    if (preferences.getBoolean("pref_change_animation", false)) {
                        Random rand = new Random();
                        if (rand.nextBoolean()) {
                            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                        } else {
                            viewPager.setPageTransformer(true, new DepthPageTransformer());
                        }
                    }
                }
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("pref_automatic_swipe", false))
            pageSwitcher(Integer.valueOf(preferences.getString("pref_timeout", "5")));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_settings);
        }
        setContentView(R.layout.activity_main);
        viewPager = ((ViewPager) findViewById(R.id.view_pager));
        assert viewPager != null;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (viewPager.getCurrentItem() >= count - 1) {
                        viewPager.setCurrentItem(0);
                    } else {
                        if (preferences.getBoolean("pref_random", false)) {
                            int max = viewPager.getChildCount();
                            int min = 0;
                            Random rand = new Random();
                            viewPager.setCurrentItem(rand.nextInt((max - min) + 1) + min);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    }

                    if (preferences.getBoolean("pref_change_animation", false)) {
                        Random rand = new Random();
                        if (rand.nextBoolean()) {
                            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                        } else {
                            viewPager.setPageTransformer(true, new DepthPageTransformer());
                        }
                    }
                }
            });

        }
    }

}
