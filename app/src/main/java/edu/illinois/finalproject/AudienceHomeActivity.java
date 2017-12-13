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

import java.util.List;
import java.util.UUID;

import static edu.illinois.finalproject.RoomAdapter.alertDialog;

public class AudienceHomeActivity extends AppCompatActivity {

    private ListView songsList;
    private String userID;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audience_home);

        // make sure the alert dialog ends once activity reaches
        alertDialog.dismiss();

        // gets Room and Room ID that was passed through the intent
        Intent intent = getIntent();
        Room room = intent.getParcelableExtra("room");
        String roomID = intent.getStringExtra("roomID");

        // sets the title of the ActionBar for this activity to the name of the room (Party)
        setTitle(room.getName());

        // initialize fields
        songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);
        // set a unique userID for user
        userID = UUID.randomUUID().toString();

        displaySongs(roomID);
    }

    /**
     * Initialize tracks to the tracks that are within the playlist of the room
     *
     * @param roomID    String representing the id of the room
     */
    private void displaySongs(String roomID) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID);

        // updates the current playlist every time there's a change in the playlist list of tracks
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateTracks(roomID, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Updates all current tracks and populates ListView with new set of tracks
     *
     * @param roomSnapshot    DataSnapshot object representing the playlist of a particular room
     */
    private void updateTracks(String roomID, DataSnapshot roomSnapshot) {
        // sorts all tracks except 1st two by number of likes
        // the first two don't get sorted because they contain currently
        // playing track and queued track
        Room updatedRoom = roomSnapshot.getValue(Room.class);
        List<String> sortedPlayOrderIDs = updatedRoom.getSortedPlaylistIDs();
        List<SimpleTrack> sortedPlayOrderTracks = updatedRoom.getSortedPlaylistTracks();

        // populate the List of tracks View with the new set of tracks
        AudienceSongAdapter playlistAdapter = new AudienceSongAdapter(this, roomID, userID,
                sortedPlayOrderIDs, sortedPlayOrderTracks);
        songsList.setAdapter(playlistAdapter);
    }

    /**
     * Makes sure that the user cannot navigate back anymore once user reaches this activity
     */
    @Override
    public void onBackPressed() {
    }

}
