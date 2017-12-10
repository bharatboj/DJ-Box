package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.DJBoxUtils.getArtistsAsString;
import static edu.illinois.finalproject.DJBoxUtils.getNumLikesForSong;
import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.getTrackDuration;
import static edu.illinois.finalproject.DJBoxUtils.openActivity;

public class DJHomeActivity extends AppCompatActivity {

    private ListView songsList;

    private SparseArray<Track> tracks;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dj_home);

        songsList = (ListView) findViewById(R.id.lv_playlist_songs);

        PlaylistSimple playlist = getIntent().getParcelableExtra("playlist");
        String roomID = getIntent().getStringExtra("roomID");

        setTracks(playlist);
        addPlaylistTracksToDatabase(roomID);

        displaySongs(roomID);
    }

    /**
     * Adds all tracks information in playlist to Firebase Database
     *
     * @param roomID    String representing roomID of room to add playlist tracks to
     */
    private void addPlaylistTracksToDatabase(String roomID) {
        // goes through each PlaylistTrack object in playlist and creates a Map:
        // (track index -> track information)
        Map<String, Object> playlistTracks = new HashMap<>();
        for (int index = 0; index < tracks.size(); index++) {
            playlistTracks.put(String.valueOf(index), getSimpleRoomTrack(roomID, tracks.get(index)));
        }

        // add a playlist containing the songIDs in the respective room
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("playlist").updateChildren(playlistTracks);
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

        // options Map<String, Object> is used to handle offsets and limits so user can add more than
        // limit number of tracks for each API call
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 100);

        // adds all Track objects within PlaylistSimple object to a List of Track objects
        for (int offset = 0; offset < playlist.tracks.total; offset += 100) {
            options.put(SpotifyService.OFFSET, offset);
            List<PlaylistTrack> playlistTracks = spotify
                    .getPlaylistTracks(playlistOwnerID, playlistID, options).items;
            for (int index = 0; index < playlistTracks.size(); index++) {
                Track track = playlistTracks.get(index).track;
                tracks.put(index, track);
            }
        }
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
                tracks.put(Integer.parseInt(dataSnapshot.getKey()), track);
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

    private SimpleTrack getSimpleRoomTrack(String roomID, Track track) {
        return new SimpleTrack(track.id, track.name, getArtistsAsString(track.artists)
                , getTrackDuration(track.duration_ms), getNumLikesForSong(roomID, track.id)
                , track.album.images.get(0).url);
    }
}
