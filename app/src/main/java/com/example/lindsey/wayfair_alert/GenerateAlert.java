package com.example.lindsey.wayfair_alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
            Log.d(TAG, "Hello");
            this.train_info.notification = (new GenerateNotification().execute(URL)).get();
            Log.d(TAG, this.train_info.notification);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(main);
            String stop = sharedPref.getString("isDismiss", "not dismissed");
            Log.d("stop",stop);
            if (stop.equalsIgnoreCase("dismissed")) {
                Thread.sleep(20000);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("isDismiss", "not dismissed");
                editor.commit();
            }
            main.createNotification(this.train_info.notification);


        } catch (Exception e) {

        }
    }

    private class GenerateNotification extends AsyncTask<String, Void, String>{
        private String url;
        public JSONParser jsonParser = new JSONParser();

        @Override
        protected String doInBackground(String... urls) {
            Log.d(TAG, urls[0]);
            JSONObject json = this.jsonParser.getJSONFromUrl(urls[0]);
            String generate_notification = "";
            try {
                int predict_arrival_time = 0;
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

                generate_notification = trip_name + " will arrive soon " + Integer.toString(predict_arrival_time);
                Log.d(TAG, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                generate_notification = "There is no train available now";
                return generate_notification;
            }
            return generate_notification;
        }
    }
}
