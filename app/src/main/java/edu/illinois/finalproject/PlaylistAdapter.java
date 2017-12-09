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

// Used code from url below as reference:
// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> {

    private static class PlaylistViewHolder {
        TextView nameTextView;
        TextView ownerTextView;
        TextView infoTextView;
        ImageView playlistImageView;
    }

    PlaylistAdapter(Context context, List<PlaylistItem> playlists) {
        super(context, R.layout.playlist_item, playlists);
    }

    @NonNull
    @Override
    public View getView(int pos, View itemView, @NonNull ViewGroup parent) {
        PlaylistItem playlist = getItem(pos);

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

        return itemView;
    }

    private void populateViews(PlaylistViewHolder viewHolder, View itemView, PlaylistItem playlist) {
        viewHolder.nameTextView.setText(playlist.getName());
        viewHolder.ownerTextView.setText(playlist.getOwner());
        viewHolder.infoTextView.setText(playlist.getInfo());

        // load playlist image into PlaylistImageView only if playlist contains image,
        // else loads a default image Spotify normally uses
        Picasso.with(itemView.getContext()).load(playlist.getImageUrl())
                .into(viewHolder.playlistImageView);
    }

    private void openAudienceHomeOnClick(final View itemView, final PlaylistItem playlist) {
        itemView.setOnClickListener(view -> {
            final Context context = view.getContext();
            Intent djHomeIntent = new Intent(context, DJHomeActivity.class);
            djHomeIntent.putExtra("playlist", playlist);
            context.startActivity(djHomeIntent);
        });
    }
}
