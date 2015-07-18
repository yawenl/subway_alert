package com.example.lindsey.wayfair_alert;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class IndexActivity extends ActionBarActivity {
    public String go_to_work_time = "8:00";
    public String go_to_home_time = "18:00";
    public int home_station_walk_time = 0;
    public int work_station_walk_time = 0;
    public String station_name;
    public int color_code;

    public Button station_name_button;
    public Button go_to_work_time_button;
    public Button go_to_home_time_button;
    public Button time_to_work_station_button;
    public Button time_to_home_station_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        String userName = sharedPref.getString("isDismiss", "not dismissed");
        //commits your edits
        editor.commit();

        this.station_name_button = (Button)findViewById(R.id.station_name);
        this.go_to_work_time_button = (Button)findViewById(R.id.go_to_work_time);
        this.go_to_home_time_button = (Button)findViewById(R.id.go_to_home_time);
        this.time_to_work_station_button = (Button)findViewById(R.id.time_to_work_station);
        this.time_to_home_station_button = (Button)findViewById(R.id.time_to_home_station);
    }

    public void setTime(String work_time, String home_time, int time_to_work_station, int time_to_home_station) {
        this.go_to_work_time = work_time;
        this.go_to_home_time = home_time;
        this.home_station_walk_time = time_to_work_station;
        this.work_station_walk_time = time_to_home_station;
    }

    public void setLocation(String station, int color) {
        this.station_name = station;
        this.color_code = color;
    }

    public void gotoStationSetting(View view) {
        //Intent timeSettingIntent = new Intent(this, VocabActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
