package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.Spotify;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.DJBoxPlayerUtils.usePlayButtonToPlaySong;
import static edu.illinois.finalproject.DJBoxUtils.displaySongs;
import static edu.illinois.finalproject.DJBoxUtils.getSimpleTrack;
import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;

public class DJHomeActivity extends AppCompatActivity {

    private ListView songsList;

    private String roomID;
    private List<Map.Entry<String, SimpleTrack>> tracks;

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
        roomID = intent.getStringExtra("roomID");
        Room room = intent.getParcelableExtra("room");
        PlaylistSimple playlist = intent.getParcelableExtra("playlist");

        // Sets the title of the ActionBar for this activity to the name of the room (Party)
        setTitle(room.getName());

        initializePartyPlaylist(room, playlist);
        addPlaylistTracksToDatabase(roomID, room);

        ToggleButton playButton = (ToggleButton) findViewById(R.id.play_button);

        displaySongs(roomID, songsList);
        usePlayButtonToPlaySong(this, playButton, room.getCurrPlayingTrackID());
    }

    /**
     * Sets Set of PlaylistTrack objects to tracks that are contained within playlist
     *
     * @param playlist      PlaylistSimple object to obtain PlaylistTrack objects from
     */
    private void initializePartyPlaylist(Room room, PlaylistSimple playlist) {
        tracks = new ArrayList<>();

        // get SpotifyService object to allow to make API calls to Spotify
        SpotifyService spotify = getSpotifyService();

        // adds 100 Track objects within PlaylistSimple object to a List of Track objects
        // possible to add more, but decided that 100 is a reasonable limit for a playlist
        // that is going to be continuously edited during the party
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;
        List<PlaylistTrack> playlistTracks = spotify
                .getPlaylistTracks(playlistOwnerID, playlistID).items;

        for (int index = 0; index < playlistTracks.size(); index++) {
            // set the current Playing track and next in queue track
            Track track = playlistTracks.get(index).track;
            if (index == 0) {
                room.setCurrPlayingTrackID(track.id);
            } else if (index == 1) {
                room.setNextToPlayTrackID(track.id);
            }
            // add only the necessary parts of the track to use
            tracks.add(new AbstractMap.SimpleEntry<>(track.id, getSimpleTrack(track)));
        }
    }

    /**
     * Adds all tracks information in playlist to FireBase Database
     *
     * @param roomID    String representing roomID of room to add playlist tracks to
     */
    private void addPlaylistTracksToDatabase(String roomID, Room room) {
        // add a playlist containing the songIDs in the
        // respective room and updates FireBase room values
        room.updatePlaylist(tracks);
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).setValue(room);
    }

    /**
     * This function allows the user to log out from the DJ account when the "Log Out" button is
     * clicked. This will "end" the party by removing the room from the Firebase database
     *
     * @param view      View object that has actions performed when clicked on
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onLogOutButtonClicked(final View view) {
        // remove room from Firebase to indicate party has ended once dj logs out
        FirebaseDatabase.getInstance().getReference("Rooms").child(roomID).removeValue();

        Intent logoutIntent = new Intent(this, MainSignInActivity.class);

        // makes sure user cannot navigate backwards anymore
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(logoutIntent);

        finishAffinity();

        // code from: https://stackoverflow.com/questions/28998241/how-to-clear-cookies
        //      -and-cache-of-webview-on-android-when-not-in-webview
        //
        // Since a WebView is used, information about the user from previous use is always retined.
        // This allows to remove all cookies so the user is able to logout completely from the
        // application
        CookieManager.getInstance().removeAllCookie();
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    }
}
