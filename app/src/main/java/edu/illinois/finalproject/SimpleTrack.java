package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleTrack implements Parcelable {
    private String id;
    private String name;
    private String artists;
    private String duration;
    private String imageUrl;

    SimpleTrack() {
    }

    private SimpleTrack(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.artists = in.readString();
        this.duration = in.readString();
        this.imageUrl = in.readString();
    }

    SimpleTrack(String id, String name, String artists, String duration, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtists() {
        return artists;
    }

    public String getDuration() {
        return duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.artists);
        dest.writeString(this.duration);
        dest.writeString(this.imageUrl);
    }

    public static final Creator<SimpleTrack> CREATOR = new Creator<SimpleTrack>() {
        @Override
        public SimpleTrack createFromParcel(Parcel source) {
            return new SimpleTrack(source);
        }

        @Override
        public SimpleTrack[] newArray(int size) {
            return new SimpleTrack[size];
        }
    };
}
