package edu.illinois.finalproject;

import android.content.Context;
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

public class AudienceSongAdapter extends ArrayAdapter<SongItem> {
    private static class AudienceSongViewHolder {
        ImageView songImageView;
        TextView nameTextView;
        TextView artistsTextView;
        TextView numLikesTextView;
        ToggleButton likeButton;
    }

    AudienceSongAdapter(Context context, List<SongItem> songs) {
        super(context, R.layout.audience_home_song_item, songs);
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, ViewGroup parent) {
        SongItem song = getItem(pos);

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

        populateViews(viewHolder, itemView, song);

        return itemView;
    }

    private void populateViews(AudienceSongViewHolder viewHolder, View itemView, SongItem song) {
        viewHolder.nameTextView.setText(song.getName());
        viewHolder.artistsTextView.setText(song.getArtists());
        viewHolder.numLikesTextView.setText(String.valueOf(song.getNumLikes()));

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = song.getImageUrl();
        Picasso.with(itemView.getContext()).load(imageUrl).into(viewHolder.songImageView);
    }
}
