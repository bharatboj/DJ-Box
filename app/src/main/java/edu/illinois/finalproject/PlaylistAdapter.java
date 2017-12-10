package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

import static edu.illinois.finalproject.DJBoxUtils.getSpotifyService;

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class PlaylistAdapter extends ArrayAdapter<PlaylistSimple> {

    private String roomID;

    private static class PlaylistViewHolder {
        TextView nameTextView;
        TextView ownerTextView;
        TextView infoTextView;
        ImageView playlistImageView;
    }

    PlaylistAdapter(Context context, String roomID, List<PlaylistSimple> playlists) {
        super(context, R.layout.playlist_item, playlists);

        this.roomID = roomID;
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
     * Populates each Playlist view in the ListView with respective attributes
     *
     * @param viewHolder    PlaylistViewHolder object containing each of the playlist views
     * @param itemView      View object holding current Playlist View object
     * @param playlist      PlaylistSimple object containing information about playlist itself
     */
    private void populateViews(PlaylistViewHolder viewHolder, View itemView, PlaylistSimple playlist) {
        // initialize all necessary playlist information to load into listView item views
        String playlistName = playlist.name;
        String playlistCreatorInfo = "Created by: " + playlist.owner.id;
        String numTracksInPlaylist = playlist.tracks.total + " songs";
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
            List<PlaylistTrack> playlistTracks = getPlaylistTracks(playlist);
            addPlaylistTracksToDatabase(playlistTracks);

            final Context context = view.getContext();
            Intent djHomeIntent = new Intent(context, DJHomeActivity.class);
            djHomeIntent.putParcelableArrayListExtra("playlistTracks"
                    , (ArrayList<? extends Parcelable>) playlistTracks);
            context.startActivity(djHomeIntent);
        });
    }

    /**
     * Adds all trackIDs in playlist to FirebaseDatabase
     *
     * @param playlistTracks    List of PlaylistTrack objects that contain ids
     *                          to be added to database
     */
    private void addPlaylistTracksToDatabase(List<PlaylistTrack> playlistTracks) {
        // goes through each PlaylistTrack object in playlist and creates a Map:
        // (track index -> track id)
        Map<String, Object> playlistTrackIDs = new HashMap<>();
        for (int index = 0; index < playlistTracks.size(); index++) {
            playlistTrackIDs.put(String.valueOf(index), playlistTracks.get(index).track.id);
        }

        // add a playlist containing the songIDs in the respective room
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Rooms")
                .child(roomID).child("playlist");
        roomRef.updateChildren(playlistTrackIDs);
    }

    /**
     * Returns a list of PlayTrack objects that are contained within playlist
     *
     * @param playlist      PlaylistSimple object to obtain PlaylistTrack objects from
     * @return              a list of PlayTrack objects that are contained within playlist
     */
    private List<PlaylistTrack> getPlaylistTracks(PlaylistSimple playlist) {
        String playlistOwnerID = playlist.owner.id;
        String playlistID = playlist.id;
        List<PlaylistTrack> playlistTracks = new ArrayList<>();

        SpotifyService spotify = getSpotifyService();

        // options Map<String, Object> is used to handle offsets and limits so user can add more than
        // limit number of tracks for each API call
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, 100);

        // adds all Track objects within PlaylistSimple object to a List of PlaylistTrack objects
        for (int offset = 0; offset < playlist.tracks.total; offset += 100) {
            options.put(SpotifyService.OFFSET, offset);
            playlistTracks.addAll(spotify.getPlaylistTracks(playlistOwnerID, playlistID, options).items);
        }

        return playlistTracks;
    }

}
