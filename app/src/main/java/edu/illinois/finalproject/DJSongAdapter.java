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

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class DJSongAdapter extends ArrayAdapter<SimpleTrack> {
    private static class DJSongViewHolder {
        ImageView songImageView;
        TextView nameTextView;
        TextView artistsTextView;
        TextView numLikesTextView;
        TextView durationTextView;
        ToggleButton likeButton;
    }

    DJSongAdapter(Context context, String roomID, List<SimpleTrack> tracks) {
        super(context, R.layout.dj_home_song_item, tracks);
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, @NonNull ViewGroup parent) {
        SimpleTrack track = getItem(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dj_home_song_item, parent, false);
        }

        DJSongViewHolder viewHolder = new DJSongViewHolder();
        viewHolder.nameTextView = (TextView) itemView.findViewById(R.id.tv_song_name);
        viewHolder.artistsTextView = (TextView) itemView.findViewById(R.id.tv_artists);
        viewHolder.numLikesTextView = (TextView) itemView.findViewById(R.id.tv_num_likes);
        viewHolder.durationTextView = (TextView) itemView.findViewById(R.id.tv_duration);
        viewHolder.songImageView = (ImageView) itemView.findViewById(R.id.iv_song);
        viewHolder.likeButton = (ToggleButton) itemView.findViewById(R.id.tb_favorite);

        populateViews(viewHolder, pos, itemView, track);

        return itemView;
    }

    private void populateViews(DJSongViewHolder viewHolder, int pos, View itemView, SimpleTrack track) {
        viewHolder.nameTextView.setText(track.getName());
        viewHolder.artistsTextView.setText(track.getArtists());
        viewHolder.numLikesTextView.setText(track.getLikes().size());
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

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = track.getImageUrl();
        Picasso.with(itemView.getContext()).load(imageUrl).into(viewHolder.songImageView);
    }

}
