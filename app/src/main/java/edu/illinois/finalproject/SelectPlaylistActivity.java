package edu.illinois.finalproject;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

import static edu.illinois.finalproject.ActivityUtils.roomsRef;

public class SelectPlaylistActivity extends AppCompatActivity {

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

        displayPlaylists();
    }

    private void displayPlaylists() {
        final ListView roomList = (ListView) findViewById(R.id.lv_playlists_list);

        SpotifyApi api = new SpotifyApi();

        final String accessToken = "BQCy6XnCcXUrLcUbkfubhUZQMSAnaIfUXD8Zcn-30VJ6gN2ionzZGV" +
                "8VgHk24Fdt_dyo3gQGCH4_SkxkMIPQTF4RBcnCF6Cq-ILN-ov9oY6cq0D8br6ZfmxL" +
                "JHvuQxHOjV5x_mYOy4ler9Ol-aXqQ5vXoDX08KmeYTxd";
        api.setAccessToken(accessToken);
        SpotifyService spotify = api.getService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        List<String> playlists = new ArrayList<>();
        for (PlaylistSimple playlist : spotify.getMyPlaylists().items) {
            playlists.add(playlist.name);
        }

        ArrayAdapter a = new ArrayAdapter<>(this, R.layout.select_playlist_list_item
                , R.id.tv_playlist_name, playlists);

        roomList.setAdapter(a);
    }
}
