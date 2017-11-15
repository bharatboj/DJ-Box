package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

class ActivityUtils extends AppCompatActivity {

    /**
     * This function allows user to open an Activity from a different Activity
     *
     * @param packageContext    Context object containing context/information of the first activity
     *                          to leave
     * @param classToOpen       Class object containing the Activity to change screens to
     */
    static void openActivity(final Context packageContext, final Class classToOpen) {
        Intent intentToOpenActivity = new Intent(packageContext, classToOpen);
        packageContext.startActivity(intentToOpenActivity);
    }
}
