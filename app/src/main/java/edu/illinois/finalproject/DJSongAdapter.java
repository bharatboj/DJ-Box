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

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class DJSongAdapter extends ArrayAdapter<Map.Entry<String, SimpleTrack>> {
    /**
     * Inner class that represents a Holder of all the necessary
     * Views to include in a Track item for DJ Home
     */
    private static class DJSongViewHolder {
        ImageView songImageView;
        TextView nameTextView;
        TextView artistsTextView;
        TextView numLikesTextView;
        TextView durationTextView;
        ToggleButton likeButton;
    }

    /**
     * This constructor initializes fields for DJSongAdapter by making call to superclass
     *
     * @param context       Context object representing context of calling activity
     * @param tracks        List of SimpleTrack objects representing current tracks for room
     */
    DJSongAdapter(Context context, List<Map.Entry<String, SimpleTrack>> tracks) {
        super(context, R.layout.dj_home_song_item, tracks);
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
    public View getView(int pos, View itemView, @NonNull ViewGroup parent) {
        Map.Entry<String, SimpleTrack> track = getItem(pos);

        // itemView may not be null, so it is good to initialize it only if necessary
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dj_home_song_item, parent, false);
        }

        // initialize all the Views within DJSongViewHolder
        // recycling view to reduce number of internal calls
        DJSongViewHolder viewHolder = new DJSongViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_song_name);
        viewHolder.artistsTextView = (TextView) itemView.findViewById(R.id.tv_artists);
        viewHolder.numLikesTextView = (TextView) itemView.findViewById(R.id.tv_num_likes);
        viewHolder.durationTextView = (TextView) itemView.findViewById(R.id.tv_duration);
        viewHolder.songImageView = (ImageView) itemView.findViewById(R.id.iv_song);
        viewHolder.likeButton = (ToggleButton) itemView.findViewById(R.id.tb_favorite);

        populateViews(viewHolder, pos, itemView, track.getValue());

        return itemView;
    }

    /**
     * Populates each Track view in the ListView with respective attributes
     *
     * @param viewHolder    DJSongViewHolder object containing each of the playlist views
     * @param pos           current position of View in ListView
     * @param itemView      View object holding current PlaylistItem View object
     * @param track         SimpleTrack object containing information about track itself
     */
    private void populateViews(DJSongViewHolder viewHolder, int pos,
                               View itemView, SimpleTrack track) {

        // populating each View with respective information
        viewHolder.nameTextView.setText(track.getName());
        viewHolder.artistsTextView.setText(track.getArtists());
        viewHolder.numLikesTextView.setText(String.valueOf(track.getLikesCount()));
        viewHolder.durationTextView.setText(track.getDuration());

        // highlights the currently playing song, which is the first one
        if (pos == 0) {
            itemView.setBackgroundColor(Color.rgb(188, 207, 221));
            viewHolder.nameTextView.setTextColor(Color.BLACK);
            viewHolder.artistsTextView.setTextColor(Color.BLACK);
            viewHolder.numLikesTextView.setTextColor(Color.BLACK);
            viewHolder.durationTextView.setTextColor(Color.BLACK);
            viewHolder.songImageView.clearColorFilter();
            itemView.setAlpha(0.7f);
            viewHolder.likeButton.setAlpha(1.0f);
        } else {
            itemView.setBackgroundColor(Color.rgb(51, 51, 51));
            viewHolder.nameTextView.setTextColor(Color.rgb(153, 153, 153));
            viewHolder.artistsTextView.setTextColor(Color.rgb(153, 153, 153));
            viewHolder.numLikesTextView.setTextColor(Color.rgb(153, 153, 153));
            viewHolder.durationTextView.setTextColor(Color.rgb(153, 153, 153));
            itemView.setAlpha(1.0f);
        }

        // load track album image into DJ track ImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = track.getImageUrl();
        Picasso.with(itemView.getContext()).load(imageUrl).into(viewHolder.songImageView);
    }

}
