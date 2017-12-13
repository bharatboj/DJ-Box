package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Room implements Parcelable {
    private String access;
    private String dj;
    private String pass;
    private String name;
    private HashMap<String, SimpleTrack> playlist;
    private String currPlayingTrack;
    private String nextToPlayTrack;
    private Double latitude;
    private Double longitude;

    public Room() {
    }

    protected Room(Parcel in) {
        this.access = in.readString();
        this.dj = in.readString();
        this.pass = in.readString();
        this.name = in.readString();
        int playlistSize = in.readInt();
        this.playlist = new HashMap<>(playlistSize);
        for (int i = 0; i < playlistSize; i++) {
            String key = in.readString();
            SimpleTrack value = in.readParcelable(SimpleTrack.class.getClassLoader());
            this.playlist.put(key, value);
        }
        this.currPlayingTrack = in.readString();
        this.nextToPlayTrack = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public Room(String access, String dj, String pass, String name, HashMap<String,
            SimpleTrack> playlist, String currPlayingTrack, String nextToPlayTrack,
                Double latitude, Double longitude) {

        this.access = access;
        this.dj = dj;
        this.pass = pass;
        this.name = name;
        this.playlist = playlist;
        this.currPlayingTrack = currPlayingTrack;
        this.nextToPlayTrack = nextToPlayTrack;
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

    public String getName() {
        return name;
    }

    public HashMap<String, SimpleTrack> getPlaylist() {
        return playlist;
    }

    public ArrayList<SimpleTrack> getSortedPlaylistTracks() {
        ArrayList<SimpleTrack> tracks = new ArrayList<>(getSortedFutureSwappedPlaylist().keySet());

        tracks.add(0, this.playlist.get(nextToPlayTrack));
        tracks.add(0, this.playlist.get(currPlayingTrack));

        return tracks;
    }

    public ArrayList<String> getSortedPlaylistIDs() {
        ArrayList<String> trackIDs = new ArrayList<>(getSortedFutureSwappedPlaylist().values());

        trackIDs.add(0, nextToPlayTrack);
        trackIDs.add(0, currPlayingTrack);

        return trackIDs;
    }

    private TreeMap<SimpleTrack, String> getSortedFutureSwappedPlaylist() {
        Map<String, SimpleTrack> futureTracks = new HashMap<>(this.playlist);
        futureTracks.remove(currPlayingTrack);
        futureTracks.remove(nextToPlayTrack);

        Map<SimpleTrack, String> rev = new HashMap<>();
        for(Map.Entry<String, SimpleTrack> entry : futureTracks.entrySet()) {
            rev.put(entry.getValue(), entry.getKey());
        }

        return new TreeMap<>(rev);
    }

    public String getCurrPlayingTrack() {
        return currPlayingTrack;
    }

    public String getNextToPlayTrack() {
        return nextToPlayTrack;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setPlaylist(HashMap<String, SimpleTrack> playlist) {
        this.playlist = playlist;
    }

    public void setCurrPlayingTrack(String currPlayingTrack) {
        this.currPlayingTrack = currPlayingTrack;
    }

    public void setNextToPlayTrack(String nextToPlayTrack) {
        this.nextToPlayTrack = nextToPlayTrack;
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
        dest.writeString(this.name);
        dest.writeInt(this.playlist.size());
        for (Map.Entry<String, SimpleTrack> entry : this.playlist.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeString(this.currPlayingTrack);
        dest.writeString(this.nextToPlayTrack);
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
