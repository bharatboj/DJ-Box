package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static edu.illinois.finalproject.ActivityUtils.openActivity;
import static edu.illinois.finalproject.MainSignInActivity.getAccessToken;

public class CreateRoomActivity extends AppCompatActivity {

    EditText roomNameEditText;
    RadioButton privateButton;
    RadioButton publicButton;
    EditText passwordEditText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        roomNameEditText = (EditText) findViewById(R.id.et_room_name);
        privateButton = (RadioButton) findViewById(R.id.rb_private);
        publicButton = (RadioButton) findViewById(R.id.rb_public);
        passwordEditText = (EditText) findViewById(R.id.et_password);

        onCreateButtonClicked();
    }

    public void onCreateButtonClicked() {
        Button createRoomButton = (Button) findViewById(R.id.b_create_room);

        createRoomButton.setOnClickListener(view -> {
            String roomID = "R5";
            String djID = getAccessToken();
            String roomName = roomNameEditText.getText().toString();
            String pass = passwordEditText.getText().toString();
            double latitude = 40.116;
            double longitude = 88.243;

            Map<String, Object> roomVals = new HashMap<>();

            roomVals.put("DJ", djID);
            roomVals.put("Name", roomName);
            roomVals.put("lat", latitude);
            roomVals.put("long", longitude);

            // if a password exists, then access is set to private, else we know it's public
            if (passwordEditText.isEnabled()) {
                roomVals.put("Access", "Private");
                roomVals.put("Pass", pass);
            } else {
                roomVals.put("Access", "Public");
            }

            // create a roomID with proper room values
            FirebaseDatabase.getInstance().getReference("Rooms")
                    .child(roomID).updateChildren(roomVals);

            openActivity(CreateRoomActivity.this, SelectPlaylistActivity.class);
        });
    }

    public void onPrivateClicked(View view) {
        privateButton.setChecked(true);
        publicButton.setChecked(false);
        passwordEditText.setEnabled(true);
    }

    public void onPublicClicked(View view) {
        publicButton.setChecked(true);
        privateButton.setChecked(false);
        passwordEditText.setEnabled(false);
    }
}