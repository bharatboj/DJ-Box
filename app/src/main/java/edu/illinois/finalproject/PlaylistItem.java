package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistItem implements Parcelable {
    private String name;
    private String owner;
    private String info;
    private String imageUrl;

    private PlaylistItem(Parcel in) {
        this.name = in.readString();
        this.owner = in.readString();
        this.info = in.readString();
        this.imageUrl = in.readString();
    }

    PlaylistItem(String name, String owner, String info, String imageUrl) {
        this.name = name;
        this.owner = owner;
        this.info = info;
        this.imageUrl = imageUrl;
    }

    String getName() {
        return name;
    }

    String getOwner() {
        return owner;
    }

    String getInfo() {
        return info;
    }

    String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.owner);
        dest.writeString(this.info);
        dest.writeString(this.imageUrl);
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