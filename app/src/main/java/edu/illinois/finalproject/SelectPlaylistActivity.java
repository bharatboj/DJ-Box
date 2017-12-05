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
import static edu.illinois.finalproject.DJBoxUtils.openActivity;
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
        PlaylistSimple selectedPlaylist = playlists.get(chosenPlaylistPos);
        String roomID = "R5";
        String playlistOwnerID = selectedPlaylist.owner.id;
        String playlistID = selectedPlaylist.id;

        Map<String, Object> playlistSongIDs = new HashMap<>();
        List<PlaylistTrack> playlistTracks = new ArrayList<>();

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 100);

        for (int offset = 0; offset < selectedPlaylist.tracks.total; offset += 100) {
            options.put(SpotifyService.OFFSET, offset);
            playlistTracks.addAll(spotify.getPlaylistTracks(playlistOwnerID, playlistID, options).items);
        }

        for (int index = 0; index < playlistTracks.size(); index++) {
            playlistSongIDs.put(String.valueOf(index), playlistTracks.get(index).track.id);
        }

        // add a playlist containing the songIDs
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("Playlist").updateChildren(playlistSongIDs);

        openActivity(this, DJHomeActivity.class);
    }

}
