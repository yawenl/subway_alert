package com.example.lindsey.wayfair_alert;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

    public int train_arrive_work_station_hour_1 = 8;
    public int train_arrive_work_station_min_1 = 0;
    public int train_arrive_work_station_hour_2 = 8;
    public int train_arrive_work_station_min_2 = 0;

    public int next_train_arrive_work_station_hour = 8;
    public int next_train_arrive_work_station_min = 0;
    private int person_home_time_hour = 18;
    private int person_home_time_min = 0;

    private String minutes_till_next_train;
    private String minutes_till_next_next_train;

    public String work_station_name;
    public String line;
    public String direction_name;
    public int work_station_walk_time = 0;

    private TextView walking_time;
    //private Button time_to_home_station_button;
    private TextView off_work_time;
    private TextView work_station_name_view;
    private TextView work_station_direction;
    private TextView next_train_work;
    private TextView next_next_train_work;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();


    public Map<Integer, String> color_map = new HashMap<Integer, String>();

    //10.0.2.2 is the address used by the Android emulators to refer to the host address
    // change this to the IP of another host if required

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("isDismiss", "not dismissed");
        editor.commit();
        Log.d("main_ac", sharedPref.getString("isDismiss", "not dismissed"));

        startTimer();

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

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask(this);

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
    }

    public void initializeTimerTask(final MainActivity main) {

        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TrainInfo trainInfo = new TrainInfo();
                        new GenerateAlert(trainInfo, main).run();
                        getValues();
                        setTrainArriveTime();
                        setWalkTime();
                        setEndWorkTime();
                        setStationAndDirection();
                    }
                });

            }
        };
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
        
        this.direction_name = DirectionOptions.values()[sharedPref.getInt("direction", 0)].toString();
        this.line = color_map.get(sharedPref.getInt("line", 0));

        this.train_arrive_work_station_hour_1 = new Date((long)sharedPref.getInt("next_train",0)*1000).getHours();
        this.train_arrive_work_station_min_1 = new Date((long)sharedPref.getInt("next_train",0)*1000).getMinutes();
        this.train_arrive_work_station_hour_2 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getHours();
        this.train_arrive_work_station_min_2 = new Date((long)sharedPref.getInt("next_next_train",0)*1000).getMinutes();

        this.direction_name = DirectionOptions.values()[sharedPref.getInt("direction", 0)].toString();
        this.line = color_map.get(sharedPref.getInt("line", 0));
        //calculate duration in minutes between now and next train
        Date next_train_in_date = new Date((long)sharedPref.getInt("next_train",0)*1000);
        Date now_in_date = new Date();
        long diff = next_train_in_date.getTime() - now_in_date.getTime();
        this.minutes_till_next_train = Integer.toString((int) diff / (60 * 1000) % 60);
        this.minutes_till_next_train += "min";

        Date next_next_train_in_date = new Date((long)sharedPref.getInt("next_next_train",0)*1000);
        long diff2 = next_next_train_in_date.getTime() - now_in_date.getTime();
        this.minutes_till_next_next_train = Integer.toString((int)diff2 / (60 * 1000) % 60);
        this.minutes_till_next_next_train += "min";
    }

    public void setTrainArriveTime() {
        this.next_train_work = (TextView)findViewById(R.id.next_train_work);
        this.next_train_work.setText(minutes_till_next_train);
        this.next_next_train_work = (TextView)findViewById(R.id.next_next_train_work);
        this.next_next_train_work.setText(minutes_till_next_next_train);
    }

    public void setEndWorkTime() {
        this.off_work_time = (TextView) findViewById(R.id.off_work_time);
        this.off_work_time.setText(person_home_time_hour + ":" +
                ((person_home_time_min < 10) ? ("0" + person_home_time_min) : person_home_time_min));
    }

    public void setStationAndDirection() {
        this.work_station_name_view = (TextView)findViewById(R.id.work_station_name);
        this.work_station_name_view.setText(this.work_station_name);

        this.work_station_direction = (TextView)findViewById(R.id.work_station_direction);
        this.work_station_direction.setText(this.direction_name);
    }

    public void setWalkTime() {
        this.walking_time = (TextView) findViewById(R.id.walking_time);
        this.walking_time.setText(this.work_station_walk_time + "min");
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int min_to_leave = sharedPref.getInt("next_train", 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alert)
                .setColor(android.graphics.Color.rgb(255,151,81))
                .setContentTitle("When To Go:")
                .setContentText(notification)
                .setNumber(min_to_leave)
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
        mBuilder.addAction(R.drawable.skip, "skip", snoozePIntent);

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
