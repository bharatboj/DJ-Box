package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.openActivity;

public class DJHomeActivity extends AppCompatActivity {

    private ListView songsList;

    private List<Track> songs;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dj_home);

        readPlaylistSongs();
    }

    private void readPlaylistSongs() {
        DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child("Playlist");

        playlistRef.addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot playlistSnapshot) {
                SpotifyService spotify = getSpotifyService();

                // Searches each trackID in Iterable snapshot, gets associated Spotify Track for it,
                // and creates a list of type Track containing each of these tracks
                songs = StreamSupport.stream(playlistSnapshot.getChildren().spliterator()
                        , false).map(trackID -> spotify.getTrack(trackID.getValue(String.class)))
                        .collect(Collectors.toList());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displaySongs() {

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
