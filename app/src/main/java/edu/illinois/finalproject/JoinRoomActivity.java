package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static edu.illinois.finalproject.ActivityUtils.getRoomsAttributeVals;

public class JoinRoomActivity extends AppCompatActivity {

    /**
     * This function sets up the activity
     *
     * @param savedInstanceState    a Bundle object containing the activity's previously saved state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room);


        try {
            displayListOfRooms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayListOfRooms() throws Exception {
        String[] roomNames = getRoomsAttributeVals("Name");
//        String[] latitudes = getRoomsAttributeVals("lat");
//        String[] longitudes = getRoomsAttributeVals("long");

        TextView a = (TextView) findViewById(R.id.tv_rooms);
        TextView b = (TextView) findViewById(R.id.tv_room_distance);

        a.setText("defends");//roomNames[roomNames.length]);

    }
}
