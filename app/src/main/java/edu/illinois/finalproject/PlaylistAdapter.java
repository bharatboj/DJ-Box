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
public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> {
    PlaylistAdapter(Context context, List<PlaylistItem> playlists) {

        super(context, 0, playlists);
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, ViewGroup parent) {
        PlaylistItem playlist = getItem(pos);

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item
                    , parent, false);
        }

        populateViews(itemView, playlist);

        return itemView;
    }

    private void populateViews(View itemView, PlaylistItem playlist) {
        TextView nameTextView = (TextView) itemView.findViewById(R.id.tv_playlist_name);
        TextView ownerTextView = (TextView) itemView.findViewById(R.id.tv_playlist_owner);
        TextView infoTextView = (TextView) itemView.findViewById(R.id.tv_playlist_info);
        ImageView playlistImageView = (ImageView) itemView.findViewById(R.id.iv_playlist);

        nameTextView.setText(playlist.getName());
        ownerTextView.setText(playlist.getOwner());
        infoTextView.setText(playlist.getInfo());

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        String imageUrl = playlist.getImageUrl();
        Picasso.with(itemView.getContext())
                .load(imageUrl).into(playlistImageView);
    }
}
