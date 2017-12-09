package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
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

public class SelectPlaylistActivity extends AppCompatActivity {

    private ListView playlistList;

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

        playlistList = (ListView) findViewById(R.id.lv_playlists_list);
        playlists = new ArrayList<>();

        displayPlaylists();
    }

    private void displayPlaylists() {
        SpotifyService spotify = getSpotifyService();
        List<PlaylistItem> playlistItems = new ArrayList<>();
        List<PlaylistSimple> playlistsList = spotify.getMyPlaylists().items;
        for (PlaylistSimple playlist : playlistsList) {
            if (playlist.tracks.total > 0) {
                String playlistCreatorInfo = "Created by: " + playlist.owner.id;
                int playlistDurationInMins = getPlaylistLengthInMins(playlist);
                String playlistInfo = playlist.tracks.total + " songs, " +
                        (playlistDurationInMins / 60) + " hr " + (playlistDurationInMins % 60) + " min";
                String playlistImage = playlist.images.get(0).url;
                playlistItems.add(new PlaylistItem(playlist.name, playlistCreatorInfo, playlistInfo, playlistImage));
            }
        }

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(this, playlistItems);

        playlistList.setAdapter(playlistAdapter);
    }

    private int getPlaylistLengthInMins(PlaylistSimple playlist) {
        List<PlaylistTrack> playlistTracks = getPlaylistTracks(playlist);
        int length = 0;

        for (PlaylistTrack playlistTrack : playlistTracks) {
            length += playlistTrack.track.duration_ms;
        }

        return (int) Math.round(length / 60000.0);
    }

    public void addPlaylistOnceChosen(View view) {
        String roomID = getIntent().getStringExtra("roomID");
        List<PlaylistTrack> playlistTracks = new ArrayList<>();

        // goes through each PlaylistTrack object in playlist and creates a Map:
        // (track index -> track id)
        Map<String, Object> playlistSongIDs = new HashMap<>();
        for (int index = 0; index < playlistTracks.size(); index++) {
            playlistSongIDs.put(String.valueOf(index), playlistTracks.get(index).track.id);
        }

        // add a playlist containing the songIDs in the respective room
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("playlist");
        roomRef.updateChildren(playlistSongIDs);

        openActivity(this, DJHomeActivity.class);
    }

    private List<PlaylistTrack> getPlaylistTracks(PlaylistSimple playlist) {
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;

        SpotifyService spotify = getSpotifyService();

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 100);

        for (int offset = 0; offset < playlist.tracks.total; offset += 100) {
            options.put(SpotifyService.OFFSET, offset);
            playlistTracks.addAll(spotify.getPlaylistTracks(playlistOwnerID, playlistID, options).items);
        }

        return playlistTracks;
    }

}
