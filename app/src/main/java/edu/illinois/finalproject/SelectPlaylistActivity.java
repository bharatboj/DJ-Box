package edu.illinois.finalproject;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static edu.illinois.finalproject.ActivityUtils.roomsRef;
import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;

public class SelectPlaylistActivity extends AppCompatActivity {

    private ListView roomList;

    private String chosenPlaylist;

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
        chosenPlaylist = "";

        displayPlaylists();
        setPlaylistOnClick();
    }

    private void displayPlaylists() {
        SpotifyApi api = new SpotifyApi();

        final String accessToken = getAccessToken();
        api.setAccessToken(accessToken);
        SpotifyService spotify = api.getService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        List<String> playlists = new ArrayList<>();
        for (PlaylistSimple playlist : spotify.getMyPlaylists().items) {
            playlists.add(playlist.name);
        }

        ArrayAdapter playlistsAdapter = new ArrayAdapter<>(this, R.layout.select_playlist_list_item
                , R.id.tv_playlist_name, playlists);

        roomList.setAdapter(playlistsAdapter);
    }

    private void setPlaylistOnClick() {
        roomList.setOnItemClickListener((adapterView, playlistView, pos, id) ->
                chosenPlaylist = adapterView.getItemAtPosition(pos).toString());
    }

    public void oncePlaylistChosen(View view) {
        ((TextView) findViewById(R.id.tv_playlists)).setText(chosenPlaylist);


    }

}
