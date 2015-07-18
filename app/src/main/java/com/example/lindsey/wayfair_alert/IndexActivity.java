package com.example.lindsey.wayfair_alert;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class IndexActivity extends ActionBarActivity {
    public int person_work_time_hour = 8;
    public int person_work_time_min = 0;
    public int person_home_time_hour = 18;
    public int person_home_time_min = 0;

    public int train_arrive_work_station_hour;
    public int train_arrive_work_station_min;
    public int train_arrive_home_station_hour;
    public int train_arrive_home_station_min;

    public int heading_work_station_hour;
    public int heading_work_station_min;
    public int heading_home_station_hour;
    public int heading_home_station_min;

    public TextView train_come_work_text;
    public TextView train_come_home_text;

    public TextView leave_work_text;
    public TextView leave_home_text;

    public Button go_to_work_time_button;
    public Button go_to_home_time_button;

    //Not set
    public int home_station_walk_time = 0;
    public int work_station_walk_time = 0;
    public Button time_to_work_station_button;
    public Button time_to_home_station_button;
    //public String work_station_name;
    //public String home_station_name;
    //public Enum color_code;


    public Button station_name_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        this.person_work_time_hour = sharedPref.getInt("go_to_work_time_hour", 8);
        this.person_work_time_min = sharedPref.getInt("go_to_work_time_min", 0);
        this.person_home_time_hour = sharedPref.getInt("go_to_home_time_hour", 18);
        this.person_home_time_min = sharedPref.getInt("go_to_home_time_min", 0);
        this.home_station_walk_time = sharedPref.getInt("home_station_walk_time", 5);
        this.work_station_walk_time = sharedPref.getInt("work_station_walk_time", 5);
        //this.color_code = sharedPref.getInt("color_code", 5);
       // this.time_to_heading_work_station = sharedPref.getInt("time_to_heading_work_station", this.time_train_arrive_work_station+this.work_station_walk_time);
        //this.time_to_heading_home_station = sharedPref.getInt("time_to_heading_home_station", this.time_train_arrive_home_station+this.home_station_walk_time);
        //commits your edits
        editor.commit();

        //this.station_name_button = (Button)findViewById(R.id.station_name);

        this.time_to_work_station_button = (Button)findViewById(R.id.time_to_work_station);
        this.time_to_home_station_button = (Button)findViewById(R.id.time_to_home_station);


        //this.train_come_home_text = (TextView)findViewById(R.id.train_come_home);
    }

    public void setTrainArriveTime() {
        this.train_come_work_text = (TextView)findViewById(R.id.train_arrive_time_work);
        this.train_come_work_text.setText(""+train_arrive_work_station_hour+":"+
                ((train_arrive_work_station_min < 10) ? ("0"+train_arrive_work_station_min) : train_arrive_work_station_min));

        this.train_come_home_text = (TextView)findViewById(R.id.train_arrive_time_home);
        this.train_come_home_text.setText(""+train_arrive_home_station_hour+":"+
                ((train_arrive_home_station_min < 10) ? ("0"+train_arrive_home_station_min) : train_arrive_home_station_min));
    }

    public void setLeaveTime() {
        this.leave_home_text = (TextView)findViewById(R.id.person_leave_home);
        this.leave_home_text.setText(""+heading_home_station_hour+":"+
                ((heading_home_station_min < 10) ? ("0"+heading_home_station_min) : heading_home_station_min));

        this.leave_work_text = (TextView)findViewById(R.id.person_leave_work);
        this.leave_work_text.setText(""+heading_work_station_hour+":"+
                ((heading_work_station_min < 10) ? ("0"+heading_work_station_min) : heading_work_station_min));
    }

    public void setWorkAndEndWorkTime() {
        this.go_to_home_time_button = (Button)findViewById(R.id.go_to_home_time);
        this.go_to_home_time_button.setText(""+person_home_time_hour+":"+
                ((person_home_time_min < 10) ? ("0"+person_home_time_min) : person_home_time_min));

        this.go_to_work_time_button = (Button)findViewById(R.id.go_to_work_time);
        this.go_to_work_time_button.setText(""+person_work_time_hour+":"+
                ((person_work_time_min < 10) ? ("0"+person_work_time_min) : person_work_time_min));
    }

    public void setLocation(String station, int color) {
        //this.station_name = station;
        //this.color_code = color;
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
