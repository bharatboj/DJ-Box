package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

import static edu.illinois.finalproject.DJBoxUtils.openActivity;
import static edu.illinois.finalproject.DJBoxUtils.roomsRef;

public class JoinRoomActivity extends AppCompatActivity {

    private ListView roomList;
    private static int chosenRoomPos;
    private static String chosenRoomID;

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

        displayListOfRooms();
        recordChosenRoomOnClick();
    }

    private void displayListOfRooms() {
        FirebaseListAdapter roomAdapter = new FirebaseListAdapter<Rooms>
                (this, Rooms.class, R.layout.join_room_list_item, roomsRef) {

            @Override
            protected void populateView(View view, Rooms model, int position) {
                TextView nameTextView = (TextView) view.findViewById(R.id.tv_room_name);
                nameTextView.setText(model.getName());
            }
        };

        roomList.setAdapter(roomAdapter);
    }

    public void recordChosenRoomOnClick() {
        roomList.setOnItemClickListener((adapterView, playlistView, pos, id) -> chosenRoomPos = pos);

//        FirebaseDatabase.getInstance().getReference("Rooms")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot roomSnapshot) {
//                        int pos = chosenRoomPos;
//                        Iterator<DataSnapshot> roomsIDsIterables = roomSnapshot.getChildren().iterator();
//                        chosenRoomID = roomsIDsIterables.next().getKey();
//                        while(pos > 0) {
//                            chosenRoomID = roomsIDsIterables.next().getKey();
//                            pos--;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
    }

    public void onJoinButtonPressed(View view) {
        openActivity(this, AudienceHomeActivity.class);
    }

    public static String getChosenRoomID() {
        return chosenRoomID;
    }
}
