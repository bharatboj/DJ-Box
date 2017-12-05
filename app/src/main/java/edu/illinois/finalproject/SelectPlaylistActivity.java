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
        List<String> playlistOwners = new ArrayList<>();
        List<String> playlistInfo = new ArrayList<>();
        for (PlaylistSimple playlist : playlists) {
            playlistNames.add(playlist.name);
            playlistOwners.add(playlist.owner.id);
            int playlistDurationInMins = getPlaylistLengthInMins(playlist);
            playlistInfo.add(playlist.tracks.total + "songs, " + (playlistDurationInMins / 60)
                    + "hr " + (playlistDurationInMins % 60) + "min");
        }

        ArrayAdapter playlistNamesAdapter = new ArrayAdapter<>(this,
                R.layout.select_playlist_list_item, R.id.tv_playlist_name, playlistNames);
        ArrayAdapter playlistOwnersAdapter = new ArrayAdapter<>(this,
                R.layout.select_playlist_list_item, R.id.tv_playlist_owner, playlistOwners);
        ArrayAdapter playlistInfoAdapter = new ArrayAdapter<>(this,
                R.layout.select_playlist_list_item, R.id.tv_playlist_info, playlistInfo);

        roomList.setAdapter(playlistNamesAdapter);
        roomList.setAdapter(playlistOwnersAdapter);
        roomList.setAdapter(playlistInfoAdapter);
    }

    private int getPlaylistLengthInMins(PlaylistSimple playlist) {
        List<PlaylistTrack> playlistTracks = getPlaylistTracks(playlists.get(chosenPlaylistPos));
        int length = 0;

        for (PlaylistTrack playlistTrack : playlistTracks) {
            length += playlistTrack.track.duration_ms;
        }

        return (int) Math.round(length / 60000.0);
    }

    private void recordPlaylistOnClick() {
        roomList.setOnItemClickListener((adapterView, playlistView, pos, id) ->
                chosenPlaylistPos = pos);
    }

    public void addPlaylistOnceChosen(View view) {
        String roomID = "R5";
        List<PlaylistTrack> playlistTracks = getPlaylistTracks(playlists.get(chosenPlaylistPos));

        Map<String, Object> playlistSongIDs = new HashMap<>();
        for (int index = 0; index < playlistTracks.size(); index++) {
            playlistSongIDs.put(String.valueOf(index), playlistTracks.get(index).track.id);
        }

        // add a playlist containing the songIDs
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("Playlist").updateChildren(playlistSongIDs);

        openActivity(this, DJHomeActivity.class);
    }

    private List<PlaylistTrack> getPlaylistTracks(PlaylistSimple playlist) {
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;

        SpotifyService spotify = getSpotifyService();
        List<PlaylistTrack> playlistTracks = new ArrayList<>();

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 100);

        for (int offset = 0; offset < playlist.tracks.total; offset += 100) {
            options.put(SpotifyService.OFFSET, offset);
            playlistTracks.addAll(spotify.getPlaylistTracks(playlistOwnerID, playlistID, options).items);
        }

        return playlistTracks;
    }

}
