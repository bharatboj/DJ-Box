package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Hashtable;

import static edu.illinois.finalproject.DJBoxUtils.roomsRef;

public class JoinRoomActivity extends AppCompatActivity {

    private ListView roomList;
    private Hashtable<String, Room> rooms;

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
        roomList = (ListView) findViewById(R.id.lv_join_room_list);
        rooms = new Hashtable<>();

        displayListOfRooms();
    }

    private void displayListOfRooms() {
        roomsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateRoom(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateRoom(dataSnapshot);
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

    private void updateRoom(DataSnapshot roomSnap) {
        String roomID = roomSnap.getKey();
        Room room = roomSnap.getValue(Room.class);

        // HashTable used to ensure no duplicates are displayed and can associate Room with roomID
        rooms.put(roomID, room);

        RoomAdapter roomAdapter = new RoomAdapter(this, roomID, new ArrayList<>(rooms.values()));
        roomList.setAdapter(roomAdapter);
    }

}
