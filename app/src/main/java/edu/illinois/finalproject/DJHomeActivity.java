package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

import static com.spotify.sdk.android.player.SpotifyPlayer.NotificationCallback;
import static edu.illinois.finalproject.DJBoxUtils.getArtistsAsString;
import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.getTrackDuration;
import static edu.illinois.finalproject.PlaylistAdapter.nDialog;

public class DJHomeActivity extends AppCompatActivity implements
        NotificationCallback, ConnectionStateCallback {

    private ListView songsList;

    private SparseArray<SimpleTrack> tracks;
    private Player mPlayer;

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

        // ends dialog once this activity is reached
        nDialog.dismiss();

        songsList = (ListView) findViewById(R.id.lv_playlist_songs);

        Intent intent = getIntent();
        String roomID = intent.getStringExtra("roomID");
        Room room = intent.getParcelableExtra("room");
        PlaylistSimple playlist = intent.getParcelableExtra("playlist");

        setTracks(playlist);
        addPlaylistTracksToDatabase(roomID, room);

        displaySongs(roomID);
    }

    /**
     * Sets Set of PlaylistTrack objects to tracks that are contained within playlist
     *
     * @param playlist      PlaylistSimple object to obtain PlaylistTrack objects from
     */
    private void setTracks(PlaylistSimple playlist) {
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;
        tracks = new SparseArray<>();

        SpotifyService spotify = getSpotifyService();

        // adds 100 Track objects within PlaylistSimple object to a List of Track objects
        // possible to add more, but decided that 100 is a reasonable limit for a playlist
        // that is going to be continuously edited during the party
        List<PlaylistTrack> playlistTracks = spotify
                .getPlaylistTracks(playlistOwnerID, playlistID).items;
        for (int index = 0; index < playlistTracks.size(); index++) {
            Track track = playlistTracks.get(index).track;
            tracks.put(index, getSimpleRoomTrack(track));
        }
    }

    /**
     * Adds all tracks information in playlist to Firebase Database
     *
     * @param roomID    String representing roomID of room to add playlist tracks to
     */
    private void addPlaylistTracksToDatabase(String roomID, Room room) {
        // add a playlist containing the songIDs in the
        // respective room and updates Firebase room values
        room.setPlaylist(asList(tracks));
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
                tracks.put(Integer.parseInt(dataSnapshot.getKey()), getSimpleRoomTrack(track));
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
        DJSongAdapter playlistAdapter = new DJSongAdapter(this, roomID, asList(tracks));
        songsList.setAdapter(playlistAdapter);
    }

    private void playSong() {
        SpotifyService spotify = getSpotifyService();

    }

    /**
     * This function allows the user to log out from the DJ account when the "Log Out" button is
     * clicked
     *
     * @param view      View object that has actions performed when clicked on
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onLogOutButtonClicked(final View view) {
//        Intent logoutIntent = new Intent(this, DJHomeActivity.class);
//
//        // makes sure user cannot navigate backwards anymore
//        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        startActivity(logoutIntent);

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

    private SimpleTrack getSimpleRoomTrack(Track track) {
        return new SimpleTrack(track.id, track.name, getArtistsAsString(track.artists)
                , getTrackDuration(track.duration_ms), track.album.images.get(0).url);
    }

    @Override
    public void onLoggedIn() {
        ImageView playButton = (ImageView) findViewById(R.);
    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Error error) {
    }

    @Override
    public void onTemporaryError() {
    }

    @Override
    public void onConnectionMessage(String s) {
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }
}
