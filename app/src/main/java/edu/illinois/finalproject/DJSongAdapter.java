package edu.illinois.finalproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class DJSongAdapter extends ArrayAdapter<SongItem> {

    private static class DJSongViewHolder {
        ImageView songImageView;
        TextView nameTextView;
        TextView artistsTextView;
        TextView numLikesTextView;
        TextView durationTextView;
    }

    DJSongAdapter(Context context, List<SongItem> songs) {
        super(context, R.layout.dj_home_song_item, songs);
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, ViewGroup parent) {
        SongItem song = getItem(pos);

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

        populateViews(viewHolder, itemView, song);

        return itemView;
    }

    private void populateViews(DJSongViewHolder viewHolder, View itemView, SongItem song) {
        viewHolder.nameTextView.setText(song.getName());
        viewHolder.artistsTextView.setText(song.getArtists());
        viewHolder.numLikesTextView.setText(String.valueOf(song.getNumLikes()));
        viewHolder.durationTextView.setText(song.getDuration());

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = song.getImageUrl();
        Picasso.with(itemView.getContext()).load(imageUrl).into(viewHolder.songImageView);
    }
}
