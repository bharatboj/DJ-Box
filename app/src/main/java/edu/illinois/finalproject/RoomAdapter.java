package edu.illinois.finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RoomAdapter extends ArrayAdapter<Room> {

    private List<String> roomIDs;

    /**
     * Inner class to hold a ViewHolder for Room
     */
    private static class RoomViewHolder {
        TextView nameTextView;
    }

    /**
     * This constructor initializes the fields for RoomAdapter
     *
     * @param context   Context object that holds context of calling Activity
     * @param rooms     List<Room> object that contains list of Room objects
     */
    RoomAdapter(final Context context, final Hashtable<String, Room> rooms) {
        super(context, R.layout.join_room_list_item, new ArrayList<>(rooms.values()));

        this.roomIDs = new ArrayList<>(rooms.keySet());
    }

    /**
     * Returns View object populated with Room object
     *
     * @param pos           position of Room object in itemView
     * @param itemView      View object containing room list item
     * @param parent        @NonNull ViewGroup object containing all the views
     * @return              View object populated with Room object
     */
    @NonNull
    @Override
    public View getView(final int pos, View itemView, @NonNull final ViewGroup parent) {
        Room room = getItem(pos);
        String roomID = roomIDs.get(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.join_room_list_item, parent, false);
        }

        RoomViewHolder viewHolder = new RoomViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_room_name);

        populateViews(viewHolder, room);
        openAudienceHomeOnClick(itemView, roomID, room);

        return itemView;
    }

    /**
     * Populates viewHolder with the name of room
     *
     * @param viewHolder    ViewHolder object that needs to be populated
     * @param room          Room object that contains information about room
     */
    private void populateViews(final RoomViewHolder viewHolder, final Room room) {
        viewHolder.nameTextView.setText(room.getName());
    }

    private void openAudienceHomeOnClick(final View itemView, final String roomID, final Room room) {
        itemView.setOnClickListener(view -> {
            final Context context = view.getContext();
            Intent audienceHomeIntent = new Intent(context, AudienceHomeActivity.class);
            audienceHomeIntent.putExtra("room", room);
            audienceHomeIntent.putExtra("roomID", roomID);
            context.startActivity(audienceHomeIntent);

            // loads up progress bar as it transitions to next activity
            ProgressDialog nDialog = new ProgressDialog(getContext());
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Accessing Party Playlist");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        });
    }

}
