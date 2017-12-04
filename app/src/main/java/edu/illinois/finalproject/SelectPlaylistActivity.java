package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.roomsRef;

public class SelectPlaylistActivity extends AppCompatActivity {

    private ListView roomList;

    private int chosenPlaylistPos;
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

        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");
        roomList = (ListView) findViewById(R.id.lv_playlists_list);
        playlists = new ArrayList<>();

        setPlaylists();
        displayPlaylists();
        recordPlaylistOnClick();
    }

    private void setPlaylists() {
        SpotifyService spotify = getSpotifyService();
        playlists.addAll(spotify.getMyPlaylists().items);
    }

    private void displayPlaylists() {
        List<String> playlistNames = new ArrayList<>();
        for (PlaylistSimple playlist : playlists) {
            playlistNames.add(playlist.name);
        }

        ArrayAdapter playlistsAdapter = new ArrayAdapter<>(this, R.layout.select_playlist_list_item
                , R.id.tv_playlist_name, playlistNames);

        roomList.setAdapter(playlistsAdapter);
    }

    private void recordPlaylistOnClick() {
        roomList.setOnItemClickListener((adapterView, playlistView, pos, id) ->
                chosenPlaylistPos = pos);
    }

    public void addPlaylistOnceChosen(View view) {
        SpotifyService spotify = getSpotifyService();
        String userID = spotify.getMe().id;
        String playlistID = playlists.get(chosenPlaylistPos).id;
        List<PlaylistTrack> playlistTracks = spotify.getPlaylistTracks(userID, playlistID).items;

        Map<String, Object> playlistSongIDs = new HashMap<>();

        for (int index = 0; index < playlistTracks.size(); index++) {
            playlistSongIDs.put(String.valueOf(index), playlistTracks.get(index));
        }

        // add a playlist containing the songIDs
        roomsRef.child("R5").child("Playlist").updateChildren(playlistSongIDs);
    }

}
