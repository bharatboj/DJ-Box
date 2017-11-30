package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static edu.illinois.finalproject.TestUtils.*;

public class JoinRoomActivity extends AppCompatActivity {

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_sign_in);

        displayListOfRooms();
    }

    public void displayListOfRooms() {
        String[] roomIDs = getSnapshotKeysArray(roomsSnapshot);


    }
}
