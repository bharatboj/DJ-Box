package edu.illinois.finalproject;

public class PlaylistItem {
    private String name;
    private String owner;
    private String info;
    private String imageUrl;

    PlaylistItem(String name, String owner, String info, String imageUrl) {
        this.name = name;
        this.owner = owner;
        this.info = info;
        this.imageUrl = imageUrl;
    }

    String getName() {
        return name;
    }

    String getOwner() {
        return owner;
    }

    String getInfo() {
        return info;
    }

    String getImageUrl() {
        return imageUrl;
    }
}