package com.example.lindsey.wayfair_alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.TimerTask;
import com.example.lindsey.wayfair_alert.JSONParser;
import com.example.lindsey.wayfair_alert.MainActivity;

import enumPackage.DirectionOptions;

/**
 * Created by dameng on 7/18/2015.
 */
public class GenerateAlert extends TimerTask{

    public TrainInfo train_info;
    public MainActivity main;

    public static String URL = "http://realtime.mbta.com/developer/api/v2/predictionsbystop?api_key=wX9NwuHnZU2ToO7GmGR9uw&stop=place-bbsta&format=json";
    public static String TAG = GenerateAlert.class.getSimpleName();

    public GenerateAlert(TrainInfo train_info, MainActivity main){
        this.train_info = train_info;
        this.main = main;
    }

    public void run() {
        try {
            this.train_info.notification = (new GenerateNotification(main).execute(URL)).get();
            Log.d(TAG, this.train_info.notification);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
            String stop = sharedPref.getString("isDismiss", "not dismissed");
            Log.d("stop", stop);
            Date date = new Date();
            long current_time = date.getTime();
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
            long base_time = date.getTime();

            int hour = 15;
            int min = 0;
            int walk_time = 5;

            int arrival_time = sharedPref.getInt("arrival_time", 0);
            int alert_start_time = (int)(base_time/1000) + (hour * 3600 + min * 60 - 180);
            int get_to_station = (int)(current_time/1000) + walk_time * 60;
            int upper_bound = arrival_time + 15;
            int lower_bound = arrival_time - 60;

            Log.d("current time", ""+current_time);
            Log.d("alert time", ""+alert_start_time);
            Log.d("get to station", ""+get_to_station);
            Log.d("upper", ""+upper_bound);
            Log.d("lower", ""+lower_bound);

            if (stop.equalsIgnoreCase("dismissed")) {
                Thread.sleep(20000);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("isDismiss", "not dismissed");
                editor.commit();
            } else if (alert_start_time < current_time && (get_to_station > lower_bound && get_to_station < upper_bound)) {
                Log.d("", "alert!!!!!");
                main.createNotification(this.train_info.notification);
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
            try {

                String stop_id = json.getString("stop_id");
                String stop_name = json.getString("stop_name");
                String trip_name = "";
                JSONArray modes = json.getJSONArray("mode");
                for (int i = 0; i < modes.length(); ++i) {
                    JSONObject mode = (JSONObject) modes.get(i);
                    int type = mode.getInt("route_type");
                    if (type == 1){
                        JSONArray routes = mode.getJSONArray("route");
                        for (int j = 0; j < routes.length(); ++j) {
                            JSONObject route = (JSONObject) routes.get(i);
                            String route_id = route.getString("route_id");
                            JSONArray directions = route.getJSONArray("direction");
                            for (int k = 0; k < directions.length(); ++k) {
                                JSONObject direction = (JSONObject) directions.get(i);
                                int direction_id = direction.getInt("direction_id");
                                JSONArray trips = direction.getJSONArray("trip");
                                for (int m = 0; m < trips.length(); ++m) {
                                    JSONObject trip = (JSONObject) trips.get(i);
                                    predict_arrival_time = trip.getInt("pre_dt");
                                    trip_name = trip.getString("trip_name");
                                }
                            }
                        }
                    }
                }


                generate_notification = trip_name + " will arrive soon " + new Date((long)predict_arrival_time * 1000).toString();
                Log.d(TAG, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                generate_notification = "There is no train available now";
                return generate_notification;
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("arrival_time", predict_arrival_time);
            editor.commit();
            return generate_notification;
        }
    }
}
