package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class PlaylistAdapter extends ArrayAdapter<PlaylistSimple> {
    private String roomID;
    private Room room;

    /**
     * Inner class that represents a Holder of all the necessary
     * Views to include in a Playlist item for DJ Home
     */
    private static class PlaylistViewHolder {
        TextView nameTextView;
        TextView ownerTextView;
        TextView infoTextView;
        ImageView playlistImageView;
    }

    /**
     * This constructor initializes fields
     *
     * @param context       Context object representing context of calling activity
     * @param roomID        String representing the roomID of the room that was created
     * @param room          Room object representing the room that was created
     * @param playlists     List of PlaylistSimple objects representing user's current playlists
     */
    PlaylistAdapter(final Context context, final String roomID,
                    final Room room, final List<PlaylistSimple> playlists) {

        super(context, R.layout.playlist_item, playlists);

        this.roomID = roomID;
        this.room = room;
    }

    /**
     * ***JavaDoc below from: https://developer.android.com/reference/android/widget/
     *    ArrayAdapter.html#getView(int, android.view.View, android.view.ViewGroup)***
     *
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param pos           int: position of the item within the adapter's data set
     *                      of the item whose view we want.
     * @param itemView      View: the old view to reuse, if possible
     * @param parent        ViewGroup: The parent that this view will eventually be attached to
     *                      This value must never be null.
     * @return              a View that displays the data at the specified position in the data set.
     */
    @NonNull
    @Override
    public View getView(final int pos, View itemView, @NonNull final ViewGroup parent) {
        PlaylistSimple playlist = getItem(pos);

        // itemView may not be null, so it is good to initialize it only if necessary
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.playlist_item, parent, false);
        }

        // initalize all the Views within PlaylistViewHolder
        // recycling view to reduce number of internal calls
        PlaylistViewHolder viewHolder = new PlaylistViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_playlist_name);
        viewHolder.ownerTextView = (TextView) itemView.findViewById(R.id.tv_playlist_owner);
        viewHolder.infoTextView = (TextView) itemView.findViewById(R.id.tv_playlist_info);
        viewHolder.playlistImageView = (ImageView) itemView.findViewById(R.id.iv_playlist);

        populateViews(viewHolder, itemView, playlist);
        openDJHomeOnClick(itemView, playlist);

        return itemView;
    }

    /**
     * Populates each PlaylistItem view in the ListView with respective attributes
     *
     * @param viewHolder    PlaylistViewHolder object containing each of the playlist views
     * @param itemView      View object holding current PlaylistItem View object
     * @param playlist      PlaylistSimple object containing information about playlist itself
     */
    private void populateViews(PlaylistViewHolder viewHolder,
                               View itemView, PlaylistSimple playlist) {

        // initialize all necessary playlist information to load into listView item views
        String playlistName = playlist.name;
        String playlistCreatorInfo = "Created by: " + playlist.owner.id;
        int totalNumTracks = playlist.tracks.total;
        String numTracksInPlaylist = totalNumTracks + (totalNumTracks > 1 ? " songs" : " song");
        // image always exists. First index represents the largest image stored for the playlist
        String playlistImageUrl = playlist.images.get(0).url;

        // populating each View with respective information
        viewHolder.nameTextView.setText(playlistName);
        viewHolder.ownerTextView.setText(playlistCreatorInfo);
        viewHolder.infoTextView.setText(numTracksInPlaylist);

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        Picasso.with(itemView.getContext()).load(playlistImageUrl)
                .into(viewHolder.playlistImageView);
    }

    /**
     * Opens DJ Home page on playlist item click
     *
     * @param playlistItemView      View object containing an object within the ListView
     * @param playlist              PlaylistSimple object containing information about playlist
     */
    private void openDJHomeOnClick(final View playlistItemView, final PlaylistSimple playlist) {
        // if playlist is selected, then DJ can now access its songs through DJ Home Activity
        playlistItemView.setOnClickListener(view -> {
            final Context context = view.getContext();
            final Intent djHomeIntent = new Intent(context, DJHomeActivity.class);

            // makes sure user cannot navigate backwards anymore
            djHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            djHomeIntent.putExtra("roomID", roomID);
            djHomeIntent.putExtra("room", room);
            djHomeIntent.putExtra("playlist", playlist);

            context.startActivity(djHomeIntent);
        });
    }

}