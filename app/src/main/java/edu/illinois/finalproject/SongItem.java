package edu.illinois.finalproject;

public class SongItem {
    private String id;
    private String name;
    private String artists;
    private String duration;
    private int numLikes;
    private String imageUrl;

    SongItem(String id, String name, String artists, String duration, int numLikes, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.duration = duration;
        this.numLikes = numLikes;
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

    public int getNumLikes() {
        return numLikes;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
