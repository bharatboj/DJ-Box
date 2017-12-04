package edu.illinois.finalproject;

public class Room {
    private String access;
    private String dj;
    private String Name;
    private Double latitude;
    private Double longitude;

    Room() {
    }

    Room(String access, String dj, String name, Double latitude, Double longitude) {
        this.access = access;
        this.dj = dj;
        this.Name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAccess() {
        return access;
    }

    public String getDj() {
        return dj;
    }

    public String getName() {
        return Name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
