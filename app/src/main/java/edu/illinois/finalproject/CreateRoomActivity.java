package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
    public void onCreateButtonClicked(final View view) {
        // initialize all room attributes to their respective variables
        String roomID = getNewRoomID();
        String djID = getSpotifyService().getMe().id;
        String roomName = roomNameEditText.getText().toString();

        // null for now. Once app gets more complicated, will add
        // functionality to handle location
        Double latitude = null;
        Double longitude = null;

        // if a password exists, then access is set to private, else it's defaulted to public
        String pass = passwordEditText.getText().toString();
        String access;
        if (!pass.isEmpty()) {
            access = "Private";
        } else {
            access = "Public";
            // set pass to null so it doesn't show up on Firebase as an empty string
            pass = null;
        }

        // if user did not enter a name
        if (roomName.isEmpty()) {
            // if user did not also check a radioButton
            if (!privateButton.isChecked() && !publicButton.isChecked()) {
                String emptyFormText = "Please enter a name and select access.";
                Toast.makeText(this, emptyFormText, Toast.LENGTH_SHORT).show();
            }
            // if user did not also enter a password
            else if (passwordEditText.isEnabled() && pass == null) {
                String badAccessNoNameText = "Please enter a name and enter a password.";
                Toast.makeText(this, badAccessNoNameText, Toast.LENGTH_SHORT).show();
            } else {
                String emptyNameText = "Please enter a name.";
                Toast.makeText(this, emptyNameText, Toast.LENGTH_SHORT).show();
            }
        }
        // if user did not check a radio button
        else if (!roomName.isEmpty() && !privateButton.isChecked() && !publicButton.isChecked()) {
            String emptyFormText = "Please select access.";
            Toast.makeText(this, emptyFormText, Toast.LENGTH_SHORT).show();
        }
        // if user selected "Private" access but did not enter a password
        else if (!roomName.isEmpty() && passwordEditText.isEnabled() && pass == null) {
            String badAccessText = "Please enter a password.";
            Toast.makeText(this, badAccessText, Toast.LENGTH_SHORT).show();
        }
        // if successfully created room
        else {
            Room room = new Room(access, djID, pass, roomName, new HashMap<>(), null
                    , null, latitude, longitude);
            goToSelectPlaylistPage(view, roomID, room);
        }
    }

    /**
     * Allow user to select playlist next by creating intent to go to SelectPlaylistActivity
     *
     * @param view      View object representing the "Create Room" button
     * @param roomID    String representing the unique roomID associated with the room
     * @param room      Room object representing the room
     */
    private void goToSelectPlaylistPage(final View view, final String roomID, final Room room) {
        final Context context = view.getContext();
        Intent selectPlaylistIntent = new Intent(context, SelectPlaylistActivity.class);

        // pass the roomID and room to the next activity
        selectPlaylistIntent.putExtra("roomID", roomID);
        selectPlaylistIntent.putExtra("room", room);
        context.startActivity(selectPlaylistIntent);
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
     * Programmatically allows user to click the Private RadioButton
     *
     * @param view      View object representing the Private RadioButton
     */
    public void onPrivateClicked(final View view) {
        // Checks Private button on click and allows user to
        // type password for room and unchecks Public button
        privateButton.setChecked(true);
        passwordEditText.setEnabled(true);
        publicButton.setChecked(false);
    }

    /**
     * Programatically allows user to click the Public RadioButton
     *
     * @param view      View object representing the Public RadioButton
     */
    public void onPublicClicked(final View view) {
        // Checks Public button on click and unchecks Private button
        publicButton.setChecked(true);
        privateButton.setChecked(false);
        passwordEditText.setEnabled(false);
    }

    /**
     * Disables back button on Android so user cannot go to previous activity
     */
    @Override
    public void onBackPressed() {
    }
}