package com.example.dssmv_projectdroid_1230628_1222027.Model;
import java.io.Serializable;
public class Song implements Serializable{
    private String title;
    private String artist;
    private String previewUrl;
    private String imageUrl;

    public Song(String title, String artist, String previewUrl, String imageurl) {
        this.title = title;
        this.artist = artist;
        this.previewUrl = previewUrl;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getPreviewUrl() { return previewUrl; }
    public String getImageUrl() { return imageUrl; }
}
