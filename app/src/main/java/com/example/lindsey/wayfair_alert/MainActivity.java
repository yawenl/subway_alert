package com.example.lindsey.wayfair_alert;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import enumPackage.DirectionOptions;
import enumPackage.StationOptions;

public class MainActivity extends ActionBarActivity {

    public int person_home_time_hour = 18;
    public int person_home_time_min = 0;
    public int train_arrive_work_station_hour_1 = 8;
    public int train_arrive_work_station_min_1 = 0;
    public int train_arrive_work_station_hour_2 = 8;
    public int train_arrive_work_station_min_2 = 0;

    public int next_train_arrive_work_station_hour = 8;
    public int next_train_arrive_work_station_min = 0;

    public String work_station_name;
    public String line;
    public String direction_name;
    public int work_station_walk_time = 0;

    public Button time_to_work_station_button;
    public Button time_to_home_station_button;
    public Button go_to_home_time_button;
    public Button station_name_button;
    public TextView leave_work_text;
    public TextView leave_home_text;
    public TextView train_come_work_text_1;
    public TextView train_come_work_text_2;

    public Map<Integer, String> color_map = new HashMap<Integer, String>();

    public ProgressBar progressBar;
    public TrainInfo train_info;

    //10.0.2.2 is the address used by the Android emulators to refer to the host address
    // change this to the IP of another host if required

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        progressBar = (ProgressBar) findViewById(R.id.spinner);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("isDismiss", "not dismissed");
        editor.commit();
        Log.d("main_ac", sharedPref.getString("isDismiss", "not dismissed"));

        Timer timer = new Timer();
        TrainInfo train_info = new TrainInfo();
        GenerateAlert ga = new GenerateAlert(train_info, this);
        timer.schedule(ga, 0, 10000);


        Timer timer_main = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        };
        timer_main.schedule(timerTask, 30000);




        color_map.put(0, "FFAD5C");
        color_map.put(1, "94FF94");
        color_map.put(2, "A9E9FF");
        color_map.put(3, "FF5C5C");

        getValues();
        setTrainArriveTime();
        setEndWorkTime();
        setStationAndDirection();
        setWalkTime();



        // createNotification(train_info.notification);
    }

    public void getValues() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();

        this.person_home_time_hour = sharedPref.getInt("homeHour", 18);
        this.person_home_time_min = sharedPref.getInt("homeMinute", 0);
        //this.home_station_walk_time = sharedPref.getInt("home_station_walk_time", 5);
        this.work_station_walk_time = sharedPref.getInt("work_station_walk_time", 5);
        this.work_station_name = StationOptions.values()[sharedPref.getInt("station", 0)].toString();

        Log.d("aaaaaaa", StationOptions.values()[sharedPref.getInt("station", 0)].toString());
        this.direction_name = DirectionOptions.values()[sharedPref.getInt("direction", 0)].toString();
        this.line = color_map.get(sharedPref.getInt("line", 0));
        Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",this.line);
        this.train_arrive_work_station_hour_1 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getHours();
        this.train_arrive_work_station_min_1 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getMinutes();
        this.train_arrive_work_station_hour_2 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getHours();
        this.train_arrive_work_station_min_2 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getMinutes();

    }

    public void setTrainArriveTime() {
        this.train_come_work_text_1 = (TextView)findViewById(R.id.train_arrive_time_work_1);
        this.train_come_work_text_1.setText("TRAIN COMES IN "+train_arrive_work_station_hour_1 + ":" + ((train_arrive_work_station_min_1 < 10) ? ("0"+train_arrive_work_station_min_1) : train_arrive_work_station_min_1));
        this.train_come_work_text_2 = (TextView)findViewById(R.id.train_arrive_time_work_2);
        this.train_come_work_text_2.setText("TRAIN COMES IN "+train_arrive_work_station_hour_2 + ":" + ((train_arrive_work_station_min_2 < 10) ? ("0"+train_arrive_work_station_min_2) : train_arrive_work_station_min_2));
    }

    public void setEndWorkTime() {
        this.go_to_home_time_button = (Button) findViewById(R.id.go_to_home_time);
        this.go_to_home_time_button.setText("OFF WORK TIME: " + person_home_time_hour + ":" +
                ((person_home_time_min < 10) ? ("0" + person_home_time_min) : person_home_time_min));
    }

    public void setStationAndDirection() {
        this.station_name_button = (Button)findViewById(R.id.work_station_name);
        this.station_name_button.setText(this.work_station_name + " - " + this.direction_name);
        //this.station_name_button.setTextColor(Integer.parseInt(this.line,16));
    }

    public void setWalkTime() {
        this.time_to_work_station_button = (Button) findViewById(R.id.time_to_work_station);
        this.time_to_work_station_button.setText("Time needed to get to station: " + this.work_station_walk_time + " mins");
    }

    public void gotoTimeSetting(View view) {
        Intent timeSettingIntent = new Intent(this, SetStartTimeActivity.class);
        startActivity(timeSettingIntent);
    }

    public void gotoStationSetting(View view) {
        Intent stationSettingIntent = new Intent(this, SetStopAndDirectionActivity.class);
        startActivity(stationSettingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLifeCycleDemo", "onResume");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPref.getString("isDismiss", "not dismissed");

        Log.d("", userName);
    }

    public NotificationCompat.Builder createNotification(String notification) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alert)
                .setColor(android.graphics.Color.rgb(255,151,81))
                .setContentTitle("When To Go:")
                .setContentText(notification)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification));
        //go to activity
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        //dismiss
        int mNotificationId = 001;
        Intent dismissIntent = new Intent(this, DismissNotifier.class);
        dismissIntent.putExtra("notificationID", mNotificationId);
        PendingIntent dismissPIntent = PendingIntent.getActivity(this, 0, dismissIntent, 0);

        //snooze
        Intent snoozeIntent = new Intent(this, SnoozeNotifier.class);
        snoozeIntent.putExtra("notificationID", mNotificationId);
        PendingIntent snoozePIntent = PendingIntent.getActivity(this, 0, snoozeIntent, 0);

        //add to notification builder
        mBuilder.addAction(R.drawable.no, "dismiss", dismissPIntent);
        mBuilder.addAction(R.drawable.snooze, "snooze", snoozePIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        return mBuilder;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
