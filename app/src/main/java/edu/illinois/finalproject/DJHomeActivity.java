package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

import static com.spotify.sdk.android.player.SpotifyPlayer.NotificationCallback;
import static edu.illinois.finalproject.DJBoxUtils.getArtistsAsString;
import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.getTrackDuration;
import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;
import static edu.illinois.finalproject.SpotifyClient.CLIENT_ID;

public class DJHomeActivity extends AppCompatActivity implements
        NotificationCallback, ConnectionStateCallback {

    private ListView songsList;

    private HashMap<String, SimpleTrack> tracks;
    private List<String> sortedPlayOrderIDs;
    private List<SimpleTrack> sortedPlayOrderTracks;
    private Player mPlayer;
    private String currTrackID;
    private int currentTrackPosMs;
    private Handler mHandler;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dj_home);

        songsList = (ListView) findViewById(R.id.lv_playlist_songs);

        // get room, roomID, and playlist that was passed through the intents
        Intent intent = getIntent();
        String roomID = intent.getStringExtra("roomID");
        Room room = intent.getParcelableExtra("room");
        PlaylistSimple playlist = intent.getParcelableExtra("playlist");

        // Sets the title of the ActionBar for this activity to the name of the room (Party)
        setTitle(room.getName());

        setTracks(room, playlist);
        addPlaylistTracksToDatabase(roomID, room);

        displaySongs(roomID);
        currTrackID = room.getCurrPlayingTrack();
        setPlayer();

        playSong(currTrackID);
    }

    private void setPlayer() {
        Config playerConfig = new Config(this, getAccessToken(), CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mPlayer = spotifyPlayer;
                mPlayer.addConnectionStateCallback(DJHomeActivity.this);
                mPlayer.addNotificationCallback(DJHomeActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    private void playSong(String trackID) {
        ToggleButton playButton = (ToggleButton) findViewById(R.id.play_button);
        playButton.setOnCheckedChangeListener((compoundButton, isPaused) -> {
            if (!isPaused) {
                mPlayer.playUri(null, "spotify:track:" + trackID, 0, currentTrackPosMs);
                currentTrackPosMs = (int) mPlayer.getPlaybackState().positionMs;
            } else {
                mPlayer.pause(null);
            }
        });
    }

    /**
     * Sets Set of PlaylistTrack objects to tracks that are contained within playlist
     *
     * @param playlist      PlaylistSimple object to obtain PlaylistTrack objects from
     */
    private void setTracks(Room room, PlaylistSimple playlist) {
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;
        tracks = new HashMap<>();

        SpotifyService spotify = getSpotifyService();

        // adds 100 Track objects within PlaylistSimple object to a List of Track objects
        // possible to add more, but decided that 100 is a reasonable limit for a playlist
        // that is going to be continuously edited during the party
        List<PlaylistTrack> playlistTracks = spotify
                .getPlaylistTracks(playlistOwnerID, playlistID).items;
        for (int index = 0; index < playlistTracks.size(); index++) {
            Track track = playlistTracks.get(index).track;
            tracks.put(track.id, getSimpleTrack(track));
        }

        sortedPlayOrderIDs = room.getSortedPlaylistIDs();
        sortedPlayOrderTracks = room.getSortedPlaylistTracks();
    }

    /**
     * Adds all tracks information in playlist to Firebase Database
     *
     * @param roomID    String representing roomID of room to add playlist tracks to
     */
    private void addPlaylistTracksToDatabase(String roomID, Room room) {
        // add a playlist containing the songIDs in the
        // respective room and updates Firebase room values
        room.setPlaylist(tracks);
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).setValue(room);
    }

    private void displaySongs(String roomID) {
        DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID).child("playlist");

        updateQueue(roomID);
        SpotifyService spotify = getSpotifyService();

        playlistRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String trackID = dataSnapshot.getValue(String.class);
                Track track = spotify.getTrack(trackID);
                tracks.put(track.id, getSimpleTrack(track));
                updateQueue(roomID);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateQueue(String roomID) {
        // Uses an Adapter to populate the ListView DJ Home with each PlaylistTrack
//        DJSongAdapter playlistAdapter = new DJSongAdapter(this, roomID, sortedPlayOrderIDs, sortedPlayOrderTracks);
//        songsList.setAdapter(playlistAdapter);
    }

    /**
     * This function allows the user to log out from the DJ account when the "Log Out" button is
     * clicked
     *
     * @param view      View object that has actions performed when clicked on
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onLogOutButtonClicked(final View view) {
        Intent logoutIntent = new Intent(this, DJHomeActivity.class);

        // makes sure user cannot navigate backwards anymore
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(logoutIntent);

        finishAffinity();

        // Code from: https://stackoverflow.com/questions/28998241/how-to-clear-cookies
        //      -and-cache-of-webview-on-android-when-not-in-webview
        //
        // Since a WebView is used, information about the user from previous use is always retined.
        // This allows to remove all cookies so the user is able to logout completely from the
        // application
        CookieManager.getInstance().removeAllCookie();
    }

    // code below from:
    // https://stackoverflow.com/questions/17008115/how-to-convert-a-sparsearray-to-arraylist
    /**
     * Returns a List of Objects of specified Type C
     *
     * @param sparseArray   SparseArray object to convert
     * @param <C>           Type of SparseArray elements
     * @return              a List of objects of specified C
     */
    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    private SimpleTrack getSimpleTrack(Track track) {
        return new SimpleTrack(track.name, getArtistsAsString(track.artists)
                , getTrackDuration(track.duration_ms), (int) track.duration_ms, 0,
                new HashMap<>(), track.album.images.get(0).url);
    }

    @Override
    public void onLoggedIn() {
        Log.d("DJHomeActivity", "User logged in");
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onLoggedOut() {
        Log.d("DJHomeActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("DJHomeActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("DJHomeActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("DJHomeActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("DJHomeActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("DJHomeActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
