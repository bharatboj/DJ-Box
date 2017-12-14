package edu.illinois.finalproject;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;

class Room implements Parcelable {
    private String access;
    private String dj;
    private String pass;
    private String name;
    private HashMap<String, SimpleTrack> playlist;
    private String currPlayingTrackID;
    private String nextToPlayTrackID;
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
        this.currPlayingTrackID = in.readString();
        this.nextToPlayTrackID = in.readString();
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
        this.currPlayingTrackID = currPlayingTrack;
        this.nextToPlayTrackID = nextToPlayTrack;
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

    @TargetApi(Build.VERSION_CODES.N)
    public List<Map.Entry<String, SimpleTrack>> getUpdatedPlaylist(boolean currSongIsStillPlaying) {
        Map<String, SimpleTrack> futureTracksMap = new HashMap<>(playlist);

        SimpleTrack currPlayingTrack = futureTracksMap.get(currPlayingTrackID);
        SimpleTrack nextToPlayTrack = futureTracksMap.get(nextToPlayTrackID);
        futureTracksMap.remove(currPlayingTrackID);
        futureTracksMap.remove(nextToPlayTrackID);

        List<Map.Entry<String, SimpleTrack>> playlistPairs = new ArrayList<>(futureTracksMap.entrySet());
        sort(playlistPairs, (track1, track2) -> track1.getValue().compareTo(track2.getValue()));

        playlistPairs.add(0, new AbstractMap.SimpleEntry<>(nextToPlayTrackID, nextToPlayTrack));

        if (currSongIsStillPlaying) {
            playlistPairs.add(0, new AbstractMap.SimpleEntry<>(currPlayingTrackID, currPlayingTrack));
        } else {
            currPlayingTrackID = nextToPlayTrackID;
            nextToPlayTrackID = playlistPairs.get(1).getKey();
        }

        updatePlaylist(playlistPairs);

        return playlistPairs;
    }

    public String getCurrPlayingTrackID() {
        return currPlayingTrackID;
    }

    public String getNextToPlayTrackID() {
        return nextToPlayTrackID;
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

    @TargetApi(Build.VERSION_CODES.N)
    public void updatePlaylist(List<Map.Entry<String, SimpleTrack>> playlistPairs) {
        playlist.clear();
        playlistPairs.forEach(trackPair -> playlist.put(trackPair.getKey(), trackPair.getValue()));
    }

    public void setCurrPlayingTrackID(String currPlayingTrackID) {
        this.currPlayingTrackID = currPlayingTrackID;
    }

    public void setNextToPlayTrackID(String nextToPlayTrackID) {
        this.nextToPlayTrackID = nextToPlayTrackID;
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
        dest.writeString(this.currPlayingTrackID);
        dest.writeString(this.nextToPlayTrackID);
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
