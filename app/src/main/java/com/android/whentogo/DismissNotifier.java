package com.android.whentogo;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DismissNotifier extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent thisIntent = getIntent();
        nm.cancel(thisIntent.getExtras().getInt("notificationID"));

        // Create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        editor.putString("isDismiss", "dismissed");
        //commits your edits
        editor.commit();

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        finish();
    }
}
