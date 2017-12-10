package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AudienceHomeActivity extends AppCompatActivity {

    private ListView songsList;

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
        Intent intent = getIntent();
        Room room = intent.getParcelableExtra("room");
        String roomID = intent.getStringExtra("roomID");

        songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);

        displaySongs(roomID);
    }

    private void displaySongs(String roomID) {
        DatabaseReference playlistRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID).child("playlist");

        updateQueue(roomID);

        playlistRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateQueue(roomID);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateQueue(String roomID) {
        // Uses an Adapter to populate the ListView DJ Home with each PlaylistTrack
        AudienceSongAdapter playlistAdapter = new AudienceSongAdapter(this, roomID, tracks);
        songsList.setAdapter(playlistAdapter);
    }

    private String getListAsString(List<String> list) {
        String listString = list.toString();
        return listString.substring(1, listString.length() - 1);
    }

}
