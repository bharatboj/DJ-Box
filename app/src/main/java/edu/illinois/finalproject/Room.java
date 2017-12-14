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

    /**
     * Returns a list of Map.Entry<String, SimpleTrack> objects
     * representing the current queue in order
     *
     * @param currSongIsStillPlaying   boolean representing whether current Song is still playing
     * @return                         a list of Map.Entry<String, SimpleTrack> objects representing
     *                                 the current queue in order
     */
    public List<Map.Entry<String, SimpleTrack>> getUpdatedPlaylist(boolean currSongIsStillPlaying) {
        // futureTracksMap contains all tracks except the currently playing one
        // and the one that will play next
        Map<String, SimpleTrack> futureTracksMap = new HashMap<>(playlist);
        SimpleTrack currPlayingTrack = futureTracksMap.get(currPlayingTrackID);
        SimpleTrack nextToPlayTrack = futureTracksMap.get(nextToPlayTrackID);
        futureTracksMap.remove(currPlayingTrackID);
        futureTracksMap.remove(nextToPlayTrackID);

        // Convert the futureTracksMap to a List of Map.Entry<String, SimpleTrack>>
        List<Map.Entry<String, SimpleTrack>> playlistPairs = new ArrayList<>(futureTracksMap.entrySet());
        // Sort the playlist pairs in descending order based on number of likes
        sort(playlistPairs, (track1, track2) -> track1.getValue().compareTo(track2.getValue()));

        // add the next song at the start of the list if current song is over
        playlistPairs.add(0, new AbstractMap.SimpleEntry<>(nextToPlayTrackID, nextToPlayTrack));

        // if current song is still playing, then we add it to the start of the playlist
        if (currSongIsStillPlaying) {
            playlistPairs.add(0, new AbstractMap.SimpleEntry<>(currPlayingTrackID, currPlayingTrack));
        } else {
            // else we set the current track to the next track to play
            // and the next track to the song after that
            currPlayingTrackID = nextToPlayTrackID;
            nextToPlayTrackID = playlistPairs.get(1).getKey();
        }

        // update the playlist with the sorted pair values
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

    /**
     * Updates the Playlist with the new set of Map.Entry<String, SimpleTrack>values representing the
     * new playlist
     *
     * @param playlistPairs    Map.Entry<String, SimpleTrack>> object representing the list
     *                         of playlist pairs
     */
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
