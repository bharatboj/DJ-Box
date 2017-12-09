package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

import static edu.illinois.finalproject.DJBoxUtils.getArtistsAsString;
import static edu.illinois.finalproject.DJBoxUtils.getDurationAsString;
import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;

public class AudienceHomeActivity extends AppCompatActivity {

    private ListView songsList;
    private DatabaseReference roomsRef;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audience_home);

        // gets Room that was passed through the intent
        Room room = getIntent().getParcelableExtra("room");

        songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);

        updateSongs(songsList, room.getPlaylist());
    }

    private void updateSongs(ListView songsList, List<String> trackIDs) {
        SpotifyService spotify = getSpotifyService();
        List<Track> tracks = new ArrayList<>();

        spotify.getTracks(getListAsString(trackIDs));

        displaySongs(songsList, tracks);
    }

    private void displaySongs(ListView listView, List<Track> tracks) {
        List<SongItem> songItems = new ArrayList<>();

        for (Track track : tracks) {
            songItems.add(new SongItem(track.id, track.name, getArtistsAsString(track.artists)
                    , getDurationAsString(track.duration_ms), 0, track.album.images.get(0).url));
        }

        ArrayAdapter<SongItem> songAdapter = new AudienceSongAdapter(this, songItems);

        listView.setAdapter(songAdapter);
    }

    private String getListAsString(List<String> list) {
        String listString = list.toString();
        return listString.substring(1, listString.length() - 1);
    }

}
