package com.example.lindsey.wayfair_alert;


import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;


import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private JSONParser jsonParser;
    //10.0.2.2 is the address used by the Android emulators to refer to the host address
    // change this to the IP of another host if required
    private static String ageURL = "https://raw.githubusercontent.com/dm37537/Career-Matcher/master/App/test.json";
    private static String TAG = MainActivity.class.getSimpleName();
    protected String notification;
    protected JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fix the issue Android changes the font of password fields into monospace
        EditText passField = (EditText) findViewById(R.id.pword);
        passField.setTypeface(Typeface.DEFAULT);

        progressBar = (ProgressBar) findViewById(R.id.spinner);

        jsonParser = new JSONParser();
        try {
            this.notification = (new GenerateNotification(ageURL).execute()).get();
        }catch (Exception e) {
            
        }

        Log.d(TAG, this.notification);
        createNotification(this.notification);
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
                .setContentTitle("Subway Alert:")
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

    /**
     * Callback function for the Login Button. Synchronously signs in the user.
     * @param view
     */
    public void logIn(View view) {
        //prevent multiple login clicks
        if (progressBar.getVisibility() == View.VISIBLE)
            return;
        progressBar.setVisibility(View.VISIBLE);

        EditText usernameField = (EditText) findViewById(R.id.uname);
        EditText passField = (EditText) findViewById(R.id.pword);

        //Use AsyncTask to enable built-in Espresso support for testing async operations
        AsyncTask<String, Void, String> loginTask = createLoginAsyncTask();
        loginTask.execute(usernameField.getText().toString(), passField.getText().toString());
    }

    /**
     * Private function that creates an async task for login.
     * @return AsyncTask
     */
    private AsyncTask<String, Void, String> createLoginAsyncTask() {

        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
               /* try {
                    ParseUser.logIn(params[0], params[1]);
                } catch (ParseException e) {
                    return e.getMessage();
                }*/
                return null;
            }

            /**
             * Called on UI thread to perform the UI updates after the login operation finishes
             * in the backend.
             * @param errorMsg The returned string for a possible login error. A value of null
             *                 indicates login success.
             */
            @Override
            protected void onPostExecute(String errorMsg) {
                progressBar.setVisibility(View.GONE);
                if (errorMsg == null) { //login success then launch main activity
                    Intent mainIntent = new Intent(MainActivity.this, IndexActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Log.d("MyApp", errorMsg);
                    Utility.warningDialog(MainActivity.this, "Login Failed", errorMsg);
                }
            }
        };
    }

    private class GenerateNotification extends AsyncTask<String, Void, String> implements Runnable{
        private String url;

        public GenerateNotification(String url) {
            this.url = url;
        }
        public void run() {
            doInBackground();
        }
        protected String doInBackground(String... urls) {
            json = jsonParser.getJSONFromUrl(this.url);
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

                generate_notification = "Train from " + trip_name + " will arrive soon";
                Log.d(TAG, json.toString());
                Log.d(TAG, generate_notification);
            } catch (Exception e) {
                e.printStackTrace();
                generate_notification = "There is no train available now";
                return generate_notification;
            }
            return generate_notification;
        }
    }

    public JSONObject getAge(){
        JSONObject json = jsonParser.getJSONFromUrl(ageURL);
        return json;
    }

    /**
     * Callback function for signUp button. Triggers an Intent to go to the SignUpActivity.
     * @param view
     */
    public void gotoSignUp(View view) {
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
        finish();   //destroy activity to prevent going back to sign-in screen
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
