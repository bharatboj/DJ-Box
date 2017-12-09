package edu.illinois.finalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RoomAdapter extends ArrayAdapter<Room> {
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
    RoomAdapter(Context context, List<Room> rooms) {
        super(context, R.layout.join_room_list_item, rooms);
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
    public View getView(int pos, View itemView, @NonNull ViewGroup parent) {
        Room room = getItem(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.join_room_list_item, parent, false);
        }

        RoomViewHolder viewHolder = new RoomViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_room_name);

        populateViews(viewHolder, room);

        return itemView;
    }

    /**
     * Populates viewHolder with the name of room
     *
     * @param viewHolder    ViewHolder object that needs to be populated
     * @param room          Room object that contains information about room
     */
    private void populateViews(RoomViewHolder viewHolder, Room room) {
        viewHolder.nameTextView.setText(room.getName());
    }

}
