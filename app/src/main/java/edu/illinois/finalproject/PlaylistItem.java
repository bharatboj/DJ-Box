package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class PlaylistItem implements Parcelable {
    private HashMap<String, SimpleTrack> playlist;

    PlaylistItem() {
    }

    private PlaylistItem(Parcel in) {
        this.playlist = (HashMap<String, SimpleTrack>) in.readSerializable();
    }

    public PlaylistItem(HashMap<String, SimpleTrack> playlist) {
        this.playlist = playlist;
    }

    public HashMap<String, SimpleTrack> getPlaylist() {
        return playlist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.playlist);
    }

    public static final Creator<PlaylistItem> CREATOR = new Creator<PlaylistItem>() {
        @Override
        public PlaylistItem createFromParcel(Parcel source) {
            return new PlaylistItem(source);
        }

        @Override
        public PlaylistItem[] newArray(int size) {
            return new PlaylistItem[size];
        }
    };
}