package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.openActivity;
import static edu.illinois.finalproject.DJBoxUtils.roomsRef;

public class DJHomeActivity extends AppCompatActivity {

    private ListView songsList;
    private ToggleButton likeButton;

    private Map<Track, Integer> songs;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dj_home);

        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");
        songsList = (ListView) findViewById(R.id.lv_playlist_songs);
        songs = new HashMap<>();

        updateSongs();
    }

    private void updateSongs() {
        String roomID = "R5";
        DatabaseReference roomRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID);

        roomRef.child("Playlist").addValueEventListener(new ValueEventListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot roomSnapshot) {
                SpotifyService spotify = getSpotifyService();

                // Searches each trackID in Iterable snapshot, gets associated Spotify Track for it,
                // and creates a list of type Track containing each of these tracks

                StreamSupport.stream(roomSnapshot.getChildren().spliterator()
                        , false).forEach(trackID -> songs.put(spotify.getTrack(trackID.getValue(String.class))
                        , getNumLikesForSong(roomSnapshot, trackID.getValue(String.class))));

                displaySongs();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displaySongs() {
        List<SongItem> songItems = new ArrayList<>();
        for (Track track : songs.keySet()) {
            songItems.add(new SongItem(track.id, track.name, getArtistsAsString(track.artists)
                    , getDurationAsString(track.duration_ms), songs.get(track)
                    , track.album.images.get(0).url));
        }

        SongAdapter songAdapter = new SongAdapter(this, songItems);

        songsList.setAdapter(songAdapter);
    }

    private String getArtistsAsString(List<ArtistSimple> artistList) {
        StringBuilder str = new StringBuilder();

        for (ArtistSimple artist : artistList) {
            str.append(artist.name).append(", ");
        }

        str.setLength(str.length() - 2);
        return str.toString();
    }

    private int getNumLikesForSong(DataSnapshot roomSnapshot, String trackID) {
        return (int) roomSnapshot.child("Songs").child(trackID).getChildrenCount();
    }

    private String getDurationAsString(long durationMs) {
        int durationInMins = (int) durationMs / 60000;
        int durationInSecs = (int) durationMs % 60000 / 1000;

        return durationInMins + ":" + durationInSecs;
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
