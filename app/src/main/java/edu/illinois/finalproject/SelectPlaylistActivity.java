package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;

public class SelectPlaylistActivity extends AppCompatActivity {

    private ListView playlistList;

    List<PlaylistSimple> playlists;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_playlist);

        playlistList = (ListView) findViewById(R.id.lv_playlists_list);
        playlists = new ArrayList<>();

        displayPlaylists();
    }

    /**
     * Displays all the user's playlists in the ListView
     */
    @TargetApi(Build.VERSION_CODES.N)
    private void displayPlaylists() {
        // get roomID that was passed through intent
        String roomID = getIntent().getStringExtra("roomID");

        // get SpotifyService to make API calls
        SpotifyService spotify = getSpotifyService();

        List<PlaylistSimple> playlists = spotify.getMyPlaylists().items;
        playlists.removeIf(playlist -> playlist.tracks.total == 0);

        // Uses an Adapter to populate the ListView playlistList with each PlaylistSimpleObject
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, roomID, playlists);
        playlistList.setAdapter(playlistAdapter);
    }



}
