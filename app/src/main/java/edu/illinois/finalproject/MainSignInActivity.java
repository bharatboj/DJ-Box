package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static edu.illinois.finalproject.DJBoxUtils.openActivity;
import static edu.illinois.finalproject.SpotifyClient.CLIENT_ID;

/**
 * A fair amount of code, such as the getAuthenticationRequest() and getRedirect url, was reused
 * for this class from:
 * https://github.com/spotify/android-auth/blob/master/auth-sample/src/main/java/com/spotify/sdk
 *      /android/authentication/sample/MainActivity.java
 */
public class MainSignInActivity extends AppCompatActivity {
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    private static String mAccessToken;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sign_in);

        // remove Title Bar of activity because I planned to use a custom design
        getSupportActionBar().hide();
    }

    /**
     * This function opens the Audience home screen when the audience wants to "Join Party"
     *
     * @param view      View object that has actions performed when clicked on
     */
    public void onAudienceButtonClicked(final View view) {
        // create intent to lead user to Join Room screen
        Intent joinRoomIntent = new Intent(this, JoinRoomActivity.class);
        startActivity(joinRoomIntent);
    }

    /**
     * This function opens the Audience DJ authentication WebView when the DJ is signed in
     *
     * @param view      View object that has actions performed when clicked on
     */
    public void onDJButtonClicked(final View view) {
        final AuthenticationRequest request =
                getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /**
     * This function opens the DJ Home screen if the user is successfully able to log in
     *
     * @param requestCode   an integer that helps identify from which intent was used previously
     * @param resultCode    represents whether operation was successful
     * @param data          Intent object that carries the result data
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            // sets mAccessToken to the access token that is returned from the response
            mAccessToken = response.getAccessToken();

            // User logged in correctly
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                openActivity(this, CreateRoomActivity.class);
            }
        }
    }

    /**
     * This function allows to retrieve certain scopes of information from the Spotify API and
     * returns a respective new AuthenticationRequest object
     *
     * @param type  AuthenticationResponse.Type object representing type of Response
     * @return      a new AuthenticationRequest object containing different information
     *              pulled from the Spotify API
     */
    private AuthenticationRequest getAuthenticationRequest(final AuthenticationResponse.Type type) {
        // probably more scopes than necessary but just added more in case required as application
        // gets more complicated
        String[] scopes = new String[]{"playlist-read-private", "playlist-read-collaborative"
                    , "playlist-modify-public", "playlist-modify-private", "streaming"
                    , "ugc-image-upload", "user-follow-modify", "user-follow-read"
                    , "user-library-read", "user-library-modify", "user-read-private"
                    , "user-read-birthdate", "user-read-email", "user-top-read"
                    , "user-read-playback-state", "user-modify-playback-state"
                    , "user-read-currently-playing", "user-read-recently-played"};

        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(scopes)
                .build();
    }

    /**
     * This function returns a Uri holding the specified information pulled from Spotify API
     *
     * @return      a Uri holding the specified information pulled from Spotify API
     */
    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }

    /**
     * Returns the Spotify Access Token
     *
     * @return      the Spotify Access Token
     */
    public static String getAccessToken() {
        return mAccessToken;
    }

}