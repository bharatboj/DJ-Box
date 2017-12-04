package edu.illinois.finalproject;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;

import static edu.illinois.finalproject.ActivityUtils.roomsRef;

public class JoinRoomActivity extends AppCompatActivity {

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room);

        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");

        displayListOfRooms();
    }

    private void displayListOfRooms() {
        final ListView roomList = (ListView) findViewById(R.id.lv_join_room_list);

        FirebaseListAdapter roomAdapter = new FirebaseListAdapter<Room>
                (this, Room.class, R.layout.join_room_list_item, roomsRef) {

            @Override
            protected void populateView(View view, Room model, int position) {
                TextView nameTextView = (TextView) view.findViewById(R.id.tv_room_name);
                Location location = getLocation();
                nameTextView.setText(model.getName());
//                nameTextView.setText(String.valueOf(getLocation().getLatitude()));
            }
        };

        roomList.setAdapter(roomAdapter);
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        Location[] loc = new Location[1];
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                loc[0] = location;
            }
        });

        return loc[0];
    }
}
