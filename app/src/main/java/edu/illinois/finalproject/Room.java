package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Room implements Parcelable {
    private String access;
    private String dj;
    private Map<String, List<String>> likes;
    private String name;
    private List<String> playlist;
    private Double latitude;
    private Double longitude;

    public Room() {
    }

    private Room(Parcel in) {
        this.access = in.readString();
        this.dj = in.readString();
        int likesSize = in.readInt();
        this.likes = new HashMap<>(likesSize);
        for (int i = 0; i < likesSize; i++) {
            String key = in.readString();
            List<String> value = in.createStringArrayList();
            this.likes.put(key, value);
        }
        this.name = in.readString();
        this.playlist = in.createStringArrayList();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public Room(String access, String dj, Map<String, List<String>> likes, String name
            , List<String> playlist, Double latitude, Double longitude) {
        this.access = access;
        this.dj = dj;
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

    public Map<String, List<String>> getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }

    public List<String> getPlaylist() {
        return playlist;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.access);
        dest.writeString(this.dj);
        dest.writeInt(this.likes.size());
        for (Map.Entry<String, List<String>> entry : this.likes.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeStringList(entry.getValue());
        }
        dest.writeString(this.name);
        dest.writeStringList(this.playlist);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
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
