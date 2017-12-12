package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Room implements Parcelable {
    private String access;
    private String dj;
    private String pass;
    private HashMap<String, List<String>> likes;
    private String name;
    private List<SimpleTrack> playlist;
    private Double latitude;
    private Double longitude;

    public Room() {
    }

    private Room(Parcel in) {
        this.access = in.readString();
        this.dj = in.readString();
        this.pass = in.readString();
        this.likes = (HashMap<String, List<String>>) in.readSerializable();
        this.name = in.readString();
        this.playlist = new ArrayList<>();
        in.readList(this.playlist, SimpleTrack.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public Room(String access, String dj, String pass, HashMap<String, List<String>> likes, String name
            , List<SimpleTrack> playlist, Double latitude, Double longitude) {
        this.access = access;
        this.dj = dj;
        this.pass = pass;
        this.likes = likes;
        this.name = name;
        this.playlist = playlist;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAccess() {
        return access;
    }

    public String getDj() {
        return dj;
    }

    public String getPass() {
        return pass;
    }

    public Map<String, List<String>> getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }

    public List<SimpleTrack> getPlaylist() {
        return playlist;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setPlaylist(List<SimpleTrack> playlist) {
        this.playlist = playlist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.access);
        dest.writeString(this.dj);
        dest.writeString(this.pass);
        dest.writeSerializable(this.likes);
        dest.writeString(this.name);
        dest.writeList(this.playlist);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
