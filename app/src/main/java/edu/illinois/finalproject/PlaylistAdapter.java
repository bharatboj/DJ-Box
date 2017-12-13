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

    private static class PlaylistViewHolder {
        TextView nameTextView;
        TextView ownerTextView;
        TextView infoTextView;
        ImageView playlistImageView;
    }

    PlaylistAdapter(Context context, String roomID, Room room, List<PlaylistSimple> playlists) {
        super(context, R.layout.playlist_item, playlists);

        this.roomID = roomID;
        this.room = room;
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, @NonNull ViewGroup parent) {
        PlaylistSimple playlist = getItem(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.playlist_item, parent, false);
        }

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
    private void populateViews(PlaylistViewHolder viewHolder, View itemView, PlaylistSimple playlist) {
        // initialize all necessary playlist information to load into listView item views
        String playlistName = playlist.name;
        String playlistCreatorInfo = "Created by: " + playlist.owner.id;
        int totalNumTracks = playlist.tracks.total;
        String numTracksInPlaylist = totalNumTracks + (totalNumTracks > 1 ? " songs" : " song");
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
     * @param itemView      View object containing an object within the ListView
     * @param playlist      PlaylistSimple object containing information about playlist
     */
    private void openDJHomeOnClick(final View itemView, final PlaylistSimple playlist) {
        itemView.setOnClickListener(view -> {
            final Context context = view.getContext();
            Intent djHomeIntent = new Intent(context, DJHomeActivity.class);

            // makes sure user cannot navigate backwards anymore
            djHomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            djHomeIntent.putExtra("roomID", roomID);
            djHomeIntent.putExtra("room", room);
            djHomeIntent.putExtra("playlist", playlist);

            context.startActivity(djHomeIntent);
//            transitionToDJHome(context, djHomeIntent);
        });
    }

//    private void transitionToDJHome(Context context, Intent intent) {
//        // loads up progress bar as it transitions to next activity
//        nDialog = new ProgressDialog(getContext());
//        nDialog.setMessage("Loading..");
//        nDialog.setTitle("Loading Party PlaylistItem");
//        nDialog.setIndeterminate(false);
//        nDialog.setCancelable(true);
//        nDialog.show();
//
//        context.startActivity(intent);
//    }

}
