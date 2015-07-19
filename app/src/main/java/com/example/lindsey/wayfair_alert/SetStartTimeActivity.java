package com.example.lindsey.wayfair_alert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;



public class SetStartTimeActivity extends ActionBarActivity {

    Resources system;
    private TimePicker workPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_start_time);
        workToStation();
        goToWorkTime();

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

    private void workToStation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        NumberPicker numberPick = (NumberPicker) this.findViewById(R.id.eWorkToStation);
        numberPick.setMinValue(0);
        numberPick.setMaxValue(60);
        numberPick.setValue(sharedPreferences.getInt("work_station_walk_time", 0));
        numberPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void goToWorkTime(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        TimePicker timePick = (TimePicker) this.findViewById(R.id.startTimePicker);
        timePick.setCurrentHour(sharedPreferences.getInt("workHour", 0));
        timePick.setCurrentMinute(sharedPreferences.getInt("workMinute", 0));
    }

    public void SendTime(View view){
        // hour 0 ~ 24  minute: 0 ~60
        // get the go to work time
        this.workPicker = (TimePicker) this.findViewById(R.id.startTimePicker);
        int workHour = workPicker.getCurrentHour();
        int workMinute = workPicker.getCurrentMinute();

        NumberPicker numberPicker = (NumberPicker) this.findViewById(R.id.eWorkToStation);
        int eWorkToStation = numberPicker.getValue();
        // get the back home time
        /*TimePicker homePicker = (TimePicker) this.findViewById(R.id.endTimePicker);
        int homeHour = homePicker.getCurrentHour();
        int homeMinute = homePicker.getCurrentMinute();*/

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("workHour", workHour);
        editor.putInt("workMinute", workMinute);
        /*editor.putInt("homeHour", homeHour);
        editor.putInt("homeMinute", homeMinute);*/
        // estimate time from work to station
        editor.putInt("work_station_walk_time", eWorkToStation);

        Intent intent = new Intent(this, SetStopAndDirectionActivity.class);
        startActivity(intent);

        editor.commit();

        Intent intent_main = new Intent(this, MainActivity.class);
        startActivity(intent_main);

        finish();
        return;
    }

}
