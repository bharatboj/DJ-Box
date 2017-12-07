package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;

import static edu.illinois.finalproject.DJBoxUtils.roomsRef;

public class AudienceHomeActivity extends AppCompatActivity {

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audience_home);

        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");
        ListView songsList = (ListView) findViewById(R.id.lv_playlist_songs_aud);

        String roomID = "R5";
        DJBoxUtils.updateSongs(songsList, User.AUDIENCE, this);
    }

}
