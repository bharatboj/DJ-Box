package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleTrack implements Parcelable {
    private String id;
    private String name;
    private String artists;
    private String duration;
    private int durationMs;
    private String imageUrl;

    SimpleTrack() {
    }

    private SimpleTrack(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.artists = in.readString();
        this.duration = in.readString();
        this.durationMs = in.readInt();
        this.imageUrl = in.readString();
    }

    SimpleTrack(String id, String name, String artists
            , String duration, int durationMs, String imageUrl) {

        this.id = id;
        this.name = name;
        this.artists = artists;
        this.duration = duration;
        this.durationMs = durationMs;
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

    public int getDurationMs() {
        return durationMs;
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
        dest.writeInt(this.durationMs);
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
