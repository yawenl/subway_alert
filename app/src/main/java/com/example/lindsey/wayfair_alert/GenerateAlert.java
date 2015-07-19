package com.example.lindsey.wayfair_alert;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimerTask;
import com.example.lindsey.wayfair_alert.JSONParser;
import com.example.lindsey.wayfair_alert.MainActivity;

import enumPackage.DirectionOptions;
import enumPackage.LineOptions;

/**
 * Created by dameng on 7/18/2015.
 */
public class GenerateAlert extends TimerTask{

    public TrainInfo train_info;
    public MainActivity main;


    public static String TAG = GenerateAlert.class.getSimpleName();

    public GenerateAlert(TrainInfo train_info, MainActivity main){
        this.train_info = train_info;
        this.main = main;

    }

    public void run() {
        try {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
            SharedPreferences.Editor editor = sharedPref.edit();

            int hour = sharedPref.getInt("workHour", 18);
            int min = sharedPref.getInt("workMinute", 0);
            int walk_time = sharedPref.getInt("work_station_walk_time", 5);
            int line = sharedPref.getInt("line", 0);
            int direction = sharedPref.getInt("direction", 0);
            String line_name = LineOptions.values()[line].toString();
            String station_name = "";
            editor.putString("line_name", line_name);
            editor.commit();

            if (line_name.equalsIgnoreCase("orange")) {
                station_name = "place-bbsta";
            } else if (line_name.equalsIgnoreCase("green")) {
                station_name = "place-coecl";
            }

            String url = "http://realtime.mbta.com/developer/api/v2/predictionsbystop?api_key=pt6WCTS-90qfxB3R0yPYOA&stop=" + station_name + "&format=json";

            Log.d("hour", "" + hour);
            Log.d("min", ""+min);
            Log.d("line name", line_name);
            Log.d("direction", ""+direction);
            Log.d("walk time", ""+walk_time);

            this.train_info.notification = (new GenerateNotification(main).execute(url)).get();
            Log.d(TAG, this.train_info.notification);

            String stop = sharedPref.getString("isDismiss", "not dismissed");
            Log.d("stop", stop);
            Date date = new Date();
            long current_time = date.getTime();
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            long base_time = date.getTime();



            int arrival_time = sharedPref.getInt("arrival_time", 0);
            int alert_start_time = (int)(base_time/1000) + (hour * 3600 + min * 60 - 180);
            int get_to_station = (int)(current_time/1000) + walk_time * 60;
            int upper_bound = arrival_time;
            int lower_bound = arrival_time - 120;
            current_time = (int)(current_time/1000);


            Log.d("current time", ""+current_time);
            Log.d("alert time", ""+alert_start_time);
            Log.d("get to station", ""+get_to_station);
            Log.d("upper", ""+upper_bound);
            Log.d("lower", ""+lower_bound);


            if (stop.equalsIgnoreCase("dismissed")) {
                Thread.sleep(20000);
                editor = sharedPref.edit();
                editor.putString("isDismiss", "not dismissed");
                editor.commit();
            } else if (alert_start_time < current_time && (get_to_station > lower_bound && get_to_station < upper_bound)) {
                Log.d("", "alert!!!!!");
                Vibrator v = (Vibrator) main.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
                main.createNotification(this.train_info.notification);
            } else if (stop.equalsIgnoreCase("skip")) {
                Thread.sleep(20000);
                Log.d("skip", "skip");
                editor = sharedPref.edit();
                editor.putString("isDismiss", "not dismissed");
                editor.commit();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GenerateNotification extends AsyncTask<String, Void, String>{
        private String url;
        public JSONParser jsonParser = new JSONParser();
        public MainActivity main;

        public GenerateNotification(MainActivity main) {
            this.main = main;
        }

        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, urls[0]);
            JSONObject json = this.jsonParser.getJSONFromUrl(urls[0]);
            String generate_notification = "";
            int predict_arrival_time = 0;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                String stop_id = json.getString("stop_id");
                String stop_name = json.getString("stop_name");
                String trip_headsign = "";
                JSONArray modes = json.getJSONArray("mode");
                for (int i = 0; i < modes.length(); ++i) {
                    JSONObject mode = (JSONObject) modes.get(i);
                    int type = mode.getInt("route_type");
                    //type: subway for orange, bus for green
                    if (type == 1 || type == 3){
                        JSONArray routes = mode.getJSONArray("route");
                        for (int j = 0; j < routes.length(); ++j) {
                            JSONObject route = (JSONObject) routes.get(j);
                            String route_id = route.getString("route_id");
                            //route_id compares line name(orange, green) bus(39)
                            Log.d("route id", route_id);
                            if (route_id.equalsIgnoreCase(sharedPref.getString("line_name", "orange")) || (route_id.equalsIgnoreCase("39") && sharedPref.getString("line_name", "orange").equalsIgnoreCase("green"))) {
                                JSONArray directions = route.getJSONArray("direction");
                                for (int k = 0; k < directions.length(); ++k) {
                                    JSONObject direction = (JSONObject) directions.get(k);
                                    int direction_id = direction.getInt("direction_id");
                                    JSONArray trips = direction.getJSONArray("trip");
                                    //direction_id compares direction(0, 1)
                                    if (direction_id == sharedPref.getInt("direction", 0)) {
                                        for (int m = 0; m < 2 && m < trips.length(); ++m) {
                                            JSONObject trip = (JSONObject) trips.get(m);
                                            int next_arrival_time = trip.getInt("pre_dt");
                                            Log.d("next arrival", ""+next_arrival_time);
                                            if (m == 0) {
                                                editor.putInt("next_train", next_arrival_time);
                                            } else if (m == 1) {
                                                editor.putInt("next_next_train", next_arrival_time);
                                            }
                                        }
                                        editor.commit();
                                        Log.d("next", "" + sharedPref.getInt("next_train", 0));
                                        Log.d("next next", "" + sharedPref.getInt("next_next_train", 0));

                                        for (int m = 0; m < trips.length(); ++m) {
                                            JSONObject trip = (JSONObject) trips.get(m);
                                            int current_time = (int)(System.currentTimeMillis()/1000);
                                            int walk_time = sharedPref.getInt("work_station_walk_time", 5) * 60;
                                            Log.d("in loop walk_time time", ""+walk_time);
                                            if (current_time + walk_time < trip.getInt("pre_dt")) {
                                                predict_arrival_time = trip.getInt("pre_dt");
                                                trip_headsign = trip.getString("trip_headsign");
                                                Log.d("in loop arrving time", ""+predict_arrival_time);
                                                break;
                                            }
                                        }


                                    }
                                }
                            }
                        }
                    }
                }

                int train_hour = new Date((long) predict_arrival_time * 1000).getHours();
                int tran_minute = new Date((long)predict_arrival_time * 1000).getMinutes();

                String print_minute = Integer.toString(tran_minute);
                String print_hour = Integer.toString(train_hour);
                if (tran_minute < 10) {
                    print_minute = "0"+Integer.toString(tran_minute);
                }
                if (train_hour < 10) {
                    print_hour = "0"+Integer.toString(train_hour);
                }

                generate_notification = "Hurry up. Train to " + trip_headsign + " will arrive at "
                        + print_hour + ":" + print_minute;
            } catch (Exception e) {
                e.printStackTrace();
                generate_notification = "There is no train available now";
                return generate_notification;
            }

            editor.putInt("arrival_time", predict_arrival_time);
            editor.commit();
            return generate_notification;
        }
    }
}
