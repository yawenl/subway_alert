package com.example.lindsey.wayfair_alert;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;

import com.example.lindsey.wayfair_alert.Utility;


public class SignUpActivity extends ActionBarActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Fix the issue Android changes the font of password fields into monospace
        EditText passField = (EditText) findViewById(R.id.pword);
        EditText passVerifyField = (EditText) findViewById(R.id.pword2);
        passField.setTypeface(Typeface.DEFAULT);
        passVerifyField.setTypeface(Typeface.DEFAULT);

        progressBar = (ProgressBar) findViewById(R.id.spinner);
    }

    /**
     * Callback function for the SignUp Button. Synchronously signs up the user
     * with the Parse API.
     * @param view
     */
    public void signUp(View view) {
        //prevent multiple signup clicks
        if (progressBar.getVisibility() == View.VISIBLE)
            return;
        progressBar.setVisibility(View.VISIBLE);

        EditText usernameField = (EditText) findViewById(R.id.uname);
        EditText passField = (EditText) findViewById(R.id.pword);
        EditText passVerifyField = (EditText) findViewById(R.id.pword2);

        String username = usernameField.getText().toString();
        String pass = passField.getText().toString();
        String verifyPass = passVerifyField.getText().toString();

        if (username.equals("") || pass.equals("") ||
                verifyPass.equals("") || !verifyPass.equals(pass)) {
            Utility.warningDialog(this, "Bad Input", "Username/Password is not in correct format.");
            progressBar.setVisibility(View.GONE);
            return;
        }

        AsyncTask<String, Void, String> signUpTask = createSignUpAsyncTask();
        signUpTask.execute(username, pass);
    }

    /**
     * Private function that creates an async task for sign up.
     * @return AsyncTask
     */
    private AsyncTask<String, Void, String> createSignUpAsyncTask() {
        return new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                ParseUser user = new ParseUser();
                user.setUsername(params[0]);
                user.setPassword(params[1]);
                //user.setEmail("email@example.com");
                //user.put("phone", "650-555-0000");

                try {
                    user.signUp();
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
                    finish();
                } else {
                    Log.d("MyApp", errorMsg);
                    Utility.warningDialog(SignUpActivity.this, "SignUp Failed", errorMsg);
                }
            }
        };
    }

    /**
     * Callback function for the Login button. Triggers an Intent
     * to go to LoginActivity.
     * @param view
     */
    public void gotoLogin(View view) {
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
        finish();   //destroy activity to prevent going back to sign-up screen
    }
}
