package com.example.lindsey.wayfair_alert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;


public class SetStartTimeActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_start_time);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_start_time, menu);
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

    public void SendTime(View view){
        // hour 0 ~ 24  minute: 0 ~60
        // get the go to work time
        TimePicker workPicker = (TimePicker) this.findViewById(R.id.startTimePicker);
        int workHour = workPicker.getCurrentHour();
        int workMinute = workPicker.getCurrentMinute();
        // get the back home time
        TimePicker homePicker = (TimePicker) this.findViewById(R.id.endTimePicker);
        int homeHour = homePicker.getCurrentHour();
        int homeMinute = homePicker.getCurrentMinute();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("workHour", workHour);
        editor.putInt("workMinute", workMinute);
        editor.putInt("homeHour", homeHour);
        editor.putInt("homeMinute", homeMinute);
        editor.commit();



        finish();
        return;
    }
}
