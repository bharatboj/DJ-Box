package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;
import static edu.illinois.finalproject.DJBoxUtils.openActivity;

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

    public void onCreateButtonClicked(View view) {
        String roomID = "R5";
        String djID = getSpotifyService().getMe().id;
        String roomName = roomNameEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        double latitude = 40.116;
        double longitude = 88.243;

        Map<String, Object> roomVals = new HashMap<>();

        roomVals.put("dj", djID);
        roomVals.put("name", roomName);
        roomVals.put("lat", latitude);
        roomVals.put("long", longitude);

        // if a password exists, then access is set to private, else we know it's public
        if (passwordEditText.isEnabled()) {
            roomVals.put("access", "Private");
            roomVals.put("pass", pass);
        } else {
            roomVals.put("access", "Public");
        }

        // create a roomID with proper room values
        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).updateChildren(roomVals);

        openActivity(CreateRoomActivity.this, SelectPlaylistActivity.class);
    }

    public void onPrivateClicked(View view) {
        privateButton.setChecked(true);
        passwordEditText.setEnabled(true);
        publicButton.setChecked(false);
    }

    public void onPublicClicked(View view) {
        publicButton.setChecked(true);
        privateButton.setChecked(false);
        passwordEditText.setEnabled(false);
    }
}