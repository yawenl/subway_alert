package com.example.lindsey.wayfair_alert;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.AsyncTask;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Vibrator;
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

import java.util.Timer;

public class MainActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private TrainInfo train_info;

    //10.0.2.2 is the address used by the Android emulators to refer to the host address
    // change this to the IP of another host if required

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fix the issue Android changes the font of password fields into monospace
        EditText passField = (EditText) findViewById(R.id.pword);
        passField.setTypeface(Typeface.DEFAULT);

        progressBar = (ProgressBar) findViewById(R.id.spinner);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("isDismiss", "not dismissed");
        editor.commit();
        Log.d("main_ac", sharedPref.getString("isDismiss", "not dismissed"));
        Timer timer = new Timer();
        TrainInfo train_info = new TrainInfo();
        GenerateAlert ga = new GenerateAlert(train_info, this);
        timer.schedule(ga, 0, 5000);
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
                    finish();
                } else {
                    Log.d("MyApp", errorMsg);
                    Utility.warningDialog(MainActivity.this, "Login Failed", errorMsg);
                }
            }
        };
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
