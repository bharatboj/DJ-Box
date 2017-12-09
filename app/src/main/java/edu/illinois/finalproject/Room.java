package edu.illinois.finalproject;

import java.util.List;
import java.util.Map;

class Room {
    private String access;
    private String dj;
    private Map<String, List<String>> likes;
    private String name;
    private List<String> playlist;
    private Double latitude;
    private Double longitude;

    public Room() {

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

}
