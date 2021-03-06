package com.android.whentogo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import  com.android.whentogo.R;

import java.util.Arrays;

import enumPackage.DirectionOptions;
import enumPackage.LineOptions;
import enumPackage.OrangeOptions;
import enumPackage.GreenOptions;


public class SetStopAndDirectionActivity extends ActionBarActivity {

    private NumberPicker stationPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_set_stop_and_direction);

        linePicker();
        stationPicker();
        directionPicker();
    }

    private void linePicker(){
        NumberPicker linePick = (NumberPicker) this.findViewById(R.id.linePicker);
        linePick.setOnValueChangedListener(new LineValueChangeListener());
        linePick.setMinValue(0);
        linePick.setMaxValue(LineOptions.values().length - 1);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        linePick.setValue(sharedPreferences.getInt("line", 0));

        String [] line = new String[LineOptions.values().length];
        for(int i = 0; i < LineOptions.values().length; i++){
            line[i] = LineOptions.values()[i].toString();
        }

        linePick.setDisplayedValues(line);
        linePick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

    }

    public class LineValueChangeListener implements NumberPicker.OnValueChangeListener {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            String[] options;
            switch(newVal) {
                case 0:
                    options = OrangeOptions.getStringArray();
                    break;
                case 1:
                    options = GreenOptions.getStringArray();
                    break;
                default:
                    options = OrangeOptions.getStringArray();
                    break;
            }
            System.out.println(Arrays.toString(options));
            stationPicker.setDisplayedValues(null);
            stationPicker.setMaxValue(options.length - 1);
            stationPicker.setDisplayedValues(options);
        }
    }

    private void stationPicker() {
        NumberPicker stationPick = (NumberPicker) this.findViewById(R.id.stationPicker);
        stationPicker = stationPick;
        stationPicker.setMinValue(0);
        stationPicker.setMaxValue(OrangeOptions.values().length - 1);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        stationPicker.setValue(sharedPreferences.getInt("station", 0));
        String [] station = new String[OrangeOptions.values().length];
        for(int i = 0; i < OrangeOptions.values().length; i++){
            station[i] = OrangeOptions.values()[i].toString();
        }

        stationPicker.setDisplayedValues(station);
        stationPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    private void directionPicker(){
        NumberPicker directionPick = (NumberPicker) this.findViewById(R.id.directionPicker);
        directionPick.setMinValue(0);
        directionPick.setMaxValue(DirectionOptions.values().length - 1);
        String [] direction = new String[DirectionOptions.values().length];
        for(int i = 0; i < DirectionOptions.values().length; i++){
            direction[i] = DirectionOptions.values()[i].toString();
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        directionPick.setValue(sharedPreferences.getInt("direction", 0));

        directionPick.setDisplayedValues(direction);
        directionPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_stop_and_direction, menu);
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

    public void SendStationAndDirection(View view){

        // int value
        NumberPicker linePicker = (NumberPicker) this.findViewById(R.id.linePicker);
        int line = linePicker.getValue();

        NumberPicker stationPicker = (NumberPicker) this.findViewById(R.id.stationPicker);
        int station = stationPicker.getValue();

        NumberPicker directionPicker = (NumberPicker) this.findViewById(R.id.directionPicker);
        int direction = directionPicker.getValue();

        String a = LineOptions.values()[line].toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("line", line);
        editor.putInt("station", station);
        editor.putInt("direction", direction);
        Log.d("asdfa", line + "");
        Log.d("dfdfd", station + "");
        Log.d("ass", direction + "");
        editor.commit();

        finish();
    }

}
