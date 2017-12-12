package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static edu.illinois.finalproject.RoomAdapter.nDialog;

public class AudienceHomeActivity extends AppCompatActivity {

    private ListView songsList;

    List<SimpleTrack> tracks;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audience_home);

        nDialog.dismiss();

        // gets Room that was passed through the intent
        Intent intent = getIntent();
        Room room = intent.getParcelableExtra("room");
        String roomID = intent.getStringExtra("roomID");

        // Sets the title of the ActionBar for this activity to the name of the room (Party)
        setTitle(room.getName());

        songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);

        tracks = new ArrayList<>();
        setTracks(roomID);
        displaySongs(roomID);
    }

    private void setTracks(String roomID) {
        DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID).child("playlist");

        playlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot playlistSnapshot) {
                for (DataSnapshot track : playlistSnapshot.getChildren()) {
                    tracks.add(track.getValue(SimpleTrack.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displaySongs(String roomID) {
        AudienceSongAdapter playlistAdapter = new AudienceSongAdapter(this, roomID, tracks);
        songsList.setAdapter(playlistAdapter);
    }

}
