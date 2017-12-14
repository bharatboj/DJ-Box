package edu.illinois.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AudienceSongAdapter extends ArrayAdapter<Map.Entry<String, SimpleTrack>> {
    private String roomID;
    private String userID;

    /**
     * Inner class that represents a Holder of all the necessary
     * Views to include in a Track item for Audience Home
     */
    private static class AudienceSongViewHolder {
        ImageView songImageView;
        TextView nameTextView;
        TextView artistsTextView;
        TextView numLikesTextView;
        ToggleButton likeButton;
    }

    /**
     * This constructor initializes fields for AudienceSongAdapter by making call to superclass
     *
     * @param context       Context object representing context of calling activity
     * @param roomID        String representing the room ID
     * @param userID        String representing the id of the user
     * @param tracks        List of SimpleTrack objects representing current tracks for room
     */
    AudienceSongAdapter(final Context context, final String roomID, final String userID,
                        final List<Map.Entry<String, SimpleTrack>> tracks) {

        super(context, R.layout.audience_home_song_item, tracks);

        // initialize fields with values passed in teh constructor
        this.roomID = roomID;
        this.userID = userID;
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
        Map.Entry<String, SimpleTrack> trackPair = getItem(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.audience_home_song_item, parent, false);
        }

        AudienceSongViewHolder viewHolder = new AudienceSongViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_song_name_aud);
        viewHolder.artistsTextView = (TextView) itemView.findViewById(R.id.tv_artists_aud);
        viewHolder.numLikesTextView = (TextView) itemView.findViewById(R.id.tv_num_likes_aud);
        viewHolder.songImageView = (ImageView) itemView.findViewById(R.id.iv_song_aud);
        viewHolder.likeButton = (ToggleButton) itemView.findViewById(R.id.tb_favorite_aud);

        populateViews(viewHolder, pos, itemView, trackPair.getValue());
        updateLikesOnClick(viewHolder, trackPair);

        return itemView;
    }

    /**
     * Populates each Track view in the ListView with respective attributes
     *
     * @param viewHolder    AudienceSongHolder object containing each of the playlist views
     * @param pos           current position of View in ListView
     * @param itemView      View object holding current PlaylistItem View object
     * @param track         SimpleTrack object containing information about track itself
     */
    private void populateViews(final AudienceSongViewHolder viewHolder, final int pos,
                               final View itemView, final SimpleTrack track) {

        viewHolder.nameTextView.setText(track.getName());
        viewHolder.artistsTextView.setText(track.getArtists());
        viewHolder.numLikesTextView.setText(String.valueOf(track.getLikesCount()));

        if (pos == 0) {
            itemView.setBackgroundColor(Color.rgb(188, 207, 221));
            viewHolder.nameTextView.setTextColor(Color.BLACK);
            viewHolder.artistsTextView.setTextColor(Color.BLACK);
            viewHolder.numLikesTextView.setTextColor(Color.BLACK);
            viewHolder.songImageView.clearColorFilter();
            itemView.setAlpha(0.7f);
            viewHolder.likeButton.setAlpha(1.0f);
        } else {
            itemView.setBackgroundColor(Color.rgb(51, 51, 51));
            viewHolder.nameTextView.setTextColor(Color.rgb(153, 153, 153));
            viewHolder.artistsTextView.setTextColor(Color.rgb(153, 153, 153));
            viewHolder.numLikesTextView.setTextColor(Color.rgb(153, 153, 153));
            itemView.setAlpha(1.0f);
        }

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = track.getImageUrl();
        Picasso.with(itemView.getContext()).load(imageUrl).into(viewHolder.songImageView);
    }

    /**
     * Updates the likes for the different songs based on when the user clicks on them
     *
     * @param viewHolder    AudienceSongViewHolder object that contains all information
     *                      necessary for a audience track item
     * @param trackPair     Map.Entry<String, SimpleTrack> representing the song that was clicked
     */
    private void updateLikesOnClick(final AudienceSongViewHolder viewHolder,
                                    final Map.Entry<String, SimpleTrack> trackPair) {

        DatabaseReference playlistTrackRef = FirebaseDatabase.getInstance()
                .getReference("Rooms").child(roomID).child("playlist")
                .child(trackPair.getKey());

        DatabaseReference likedByRef = playlistTrackRef.child("likedBy").child(userID);

        viewHolder.likeButton.setOnCheckedChangeListener((compoundButton, isLiked) -> {
            CountDownLatch latch = new CountDownLatch(1);
            if (isLiked) {
                likedByRef.setValue(true);
                playlistTrackRef.child("likesCount").setValue(trackPair.getValue().getLikesCount() + 1);
            } else {
                likedByRef.removeValue();
                playlistTrackRef.child("likesCount").setValue(trackPair.getValue().getLikesCount() - 1);
            }
            try {
                latch.await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}