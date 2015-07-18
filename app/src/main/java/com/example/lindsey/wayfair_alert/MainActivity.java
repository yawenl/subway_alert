package com.example.lindsey.wayfair_alert;

import android.content.Intent;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lindsey.wayfair_alert.APICallActivity;
import com.example.lindsey.wayfair_alert.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import android.view.View;


public class MainActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private JSONParser jsonParser;
    //10.0.2.2 is the address used by the Android emulators to refer to the host address
    // change this to the IP of another host if required
    private static String ageURL = "https://raw.githubusercontent.com/dm37537/Career-Matcher/master/App/test.json";
    private static String getAge = "getAge";
    private static String jsonResult = "success";
    String uname;
    String age_res;
    TextView Results;
    JSONObject json;

    // temporary string to show the parsed response
    private String jsonResponse;
    private static String TAG = MainActivity.class.getSimpleName();
    private Button btnMakeObjectRequest;
    private TextView txtResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fix the issue Android changes the font of password fields into monospace
        EditText passField = (EditText) findViewById(R.id.pword);
        passField.setTypeface(Typeface.DEFAULT);

        progressBar = (ProgressBar) findViewById(R.id.spinner);
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

        jsonParser = new JSONParser();
        Results = (TextView) findViewById(R.id.txtResponse);
        new GenerateNotification(ageURL).execute();
    }

    /**
     * Private function that creates an async task for login.
     * @return AsyncTask
     */
    private AsyncTask<String, Void, String> createLoginAsyncTask() {

        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    ParseUser.logIn(params[0], params[1]);
                } catch (ParseException e) {
                    return e.getMessage();
                }
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
                    Intent mainIntent = new Intent(MainActivity.this, IndexPageActivity.class);
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
            String notification = "";
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

                notification = "Train from " + trip_name + " will arrive soon";
                Log.d(TAG, json.toString());
                Log.d(TAG, notification);
            } catch (Exception e) {
                e.printStackTrace();
                Results.setText("There is no train available now");
                return "";
            }
            Results.setText(notification);
            return notification;
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
