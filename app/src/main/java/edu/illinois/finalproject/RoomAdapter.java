package edu.illinois.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomAdapter extends ArrayAdapter<Room> {

    private List<String> roomIDs;
    static AlertDialog alertDialog;

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
    RoomAdapter(final Context context, final Map<String, Room> rooms) {
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

        // initialize all the Views within RoomViewHolder
        // recycling view to reduce number of internal calls
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

    /**
     * Opens the Audience Home Activity once the user joins the room
     * @param itemView  View object representing the room information in ListVIew
     * @param roomID    String representing the room ID
     * @param room      Room object representing the room that is clicked on
     */
    private void openAudienceHomeOnClick(final View itemView, final String roomID, final Room room) {
        itemView.setOnClickListener((View view) -> {
            final Context context = view.getContext();
            Intent audienceHomeIntent = new Intent(context, AudienceHomeActivity.class);

            // makes sure user cannot navigate backwards anymore
            audienceHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            audienceHomeIntent.putExtra("room", room);
            audienceHomeIntent.putExtra("roomID", roomID);

            // opens password if room has a password that is required
            String password = room.getPass();
            if (password != null) {
                openPasswordDialog(context, audienceHomeIntent, password);
            } else {
                context.startActivity(audienceHomeIntent);
            }
        });
    }

    /**
     * Opens the Password Dialog if the room requires a password
     *
     * @param context       Context object representing the context used to create
     *                      an intent to go to next activity
     * @param intent        Intent to go next activity
     * @param password      Correct password of the room
     */
    private void openPasswordDialog(final Context context, final Intent intent,
                                    final String password) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.password_join_room, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            // get user input and set it to result
                            // edit text
                            if (userInput.getText().toString().equals(password)) {
                                context.startActivity(intent);
                            } else {
                                String badPassText = "Incorrect Password Entered!";
                                Toast toast = Toast.makeText(context, badPassText, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> {
                            System.out.println();
                            dialog.cancel();
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // prompt dialog
        alertDialog.show();
    }
}