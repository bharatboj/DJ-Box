package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;

import static edu.illinois.finalproject.DJBoxUtils.openActivity;

public class DJHomeActivity extends AppCompatActivity {

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dj_home);

        ListView songsList = (ListView) findViewById(R.id.lv_playlist_songs);

    }

    /**
     * This function allows the user to log out from the DJ account when the "Log Out" button is
     * clicked
     *
     * @param view      View object that has actions performed when clicked on
     */
    public void onLogOutButtonClicked(final View view) {
        openActivity(this, MainSignInActivity.class);

        // Code from: https://stackoverflow.com/questions/28998241/how-to-clear-cookies
        //      -and-cache-of-webview-on-android-when-not-in-webview
        //
        // Since a WebView is used, information about the user from previous use is always retined.
        // This allows to remove all cookies so the user is able to logout completely from the
        // application
        CookieManager.getInstance().removeAllCookie();
    }
}
