package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AudienceHomeActivity extends AppCompatActivity {

    private ListView songsList;
    private List<SimpleTrack> tracks;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audience_home);

        // gets Room and Room ID that was passed through the intent
        Intent intent = getIntent();
        Room room = intent.getParcelableExtra("room");
        String roomID = intent.getStringExtra("roomID");

        // Sets the title of the ActionBar for this activity to the name of the room (Party)
        setTitle(room.getName());

        songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);

        displaySongs(roomID);
    }

    /**
     * Initialize tracks to the tracks that are within the playlist of the room
     *
     * @param roomID    String representing the id of the room
     */
    private void displaySongs(String roomID) {
        DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID).child("playlist");
        Query topSongs = playlistRef.orderByChild("likes");

        // updates the current playlist every time there's a change in the playlist list of tracks
        topSongs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot playlistSnapshot) {
                // clear tracks each time
                tracks = new ArrayList<>();
                for (DataSnapshot track : playlistSnapshot.getChildren()) {
                    // pulls all the relevant information about the track and adds to tracks list
                    tracks.add(track.getValue(SimpleTrack.class));
                }
                populateListOfTracks(roomID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Populates the List of tracks View with the new set of tracks
     *
     * @param roomID    String representing the id of the room
     */
    private void populateListOfTracks(String roomID) {
        AudienceSongAdapter playlistAdapter = new AudienceSongAdapter(this, roomID, tracks);
        songsList.setAdapter(playlistAdapter);
    }

}
