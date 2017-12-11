package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;

public class CreateRoomActivity extends AppCompatActivity {

    EditText roomNameEditText;
    RadioButton privateButton;
    RadioButton publicButton;
    EditText passwordEditText;

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        // initialize all Create Room screen views
        roomNameEditText = (EditText) findViewById(R.id.et_room_name);
        privateButton = (RadioButton) findViewById(R.id.rb_private);
        publicButton = (RadioButton) findViewById(R.id.rb_public);
        passwordEditText = (EditText) findViewById(R.id.et_password);
    }

    /**
     * Updates Firebase with information containing all attributes of new room
     *
     * @param view      View object holding the "Create" Button
     */
    public void onCreateButtonClicked(View view) {
        // initialize all room attributes to their respective variables
        String roomID = getNewRoomID();
        String djID = getSpotifyService().getMe().id;

        // if user doesn't enter a name, then the DJ's username
        // is used as room name because it is unique
        String roomName = roomNameEditText.getText().toString();
        if (roomName.isEmpty()) {
            roomName = djID;
        }

        String pass = passwordEditText.getText().toString();
        double latitude = 12.221;
        double longitude = 43.444;

        // using a Map object that will contain all the attributes for the room
        Map<String, Object> roomVals = new HashMap<>();

        roomVals.put("dj", djID);
        roomVals.put("name", roomName);
        roomVals.put("latitude", latitude);
        roomVals.put("longitude", longitude);

        // if a password exists, then access is set to private, else we know it's public
        // Note: if user decides to not click any button, it is assumed to be public
        if (passwordEditText.isEnabled()) {
            roomVals.put("access", "Private");
            roomVals.put("pass", pass);
        } else {
            roomVals.put("access", "Public");
        }

        // create a roomID with proper room values
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).updateChildren(roomVals);

        // allow user to select playlist next
        // opens SelectPlaylist screen
        final Context context = view.getContext();
        Intent audienceHomeIntent = new Intent(context, SelectPlaylistActivity.class);
        audienceHomeIntent.putExtra("roomID", roomID);
        context.startActivity(audienceHomeIntent);
    }

    /**
     * Returns a unique roomID not within Firebase list of roomIDs
     *
     * @return a unique roomID not within Firebase list of roomIDs
     */
    private String getNewRoomID() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");

        // returns a unique roomID not within Firebase list of roomIDs
        return roomsRef.push().getKey();
    }

    /**
     * Programatically allows user to click the Private RadioButton
     *
     * @param view      View object that is clicked
     */
    public void onPrivateClicked(View view) {
        // Checks Private button on click and allows user to
        // type password for room and unchecks Public button
        privateButton.setChecked(true);
        passwordEditText.setEnabled(true);
        publicButton.setChecked(false);
    }

    /**
     * Programatically allows user to click the Public RadioButton
     *
     * @param view      View object that is clicked
     */
    public void onPublicClicked(View view) {
        // Checks Public button on click and unchecks Private button
        publicButton.setChecked(true);
        privateButton.setChecked(false);
        passwordEditText.setEnabled(false);
    }
}