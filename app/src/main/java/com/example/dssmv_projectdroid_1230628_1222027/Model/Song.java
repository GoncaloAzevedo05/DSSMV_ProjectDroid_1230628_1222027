package com.example.dssmv_projectdroid_1230628_1222027.Model;
import java.io.Serializable;
public class Song implements Serializable{
    private String id;
    private String title;
    private String artist;
    private String audioUrl;;
    private String imageUrl;

    public Song() {}

    public Song(String id, String title, String artist, String audioUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.audioUrl = audioUrl;
        this.imageUrl = "";
    }

    public Song(String id, String title, String artist, String audioUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
    }


    public String getId() {return id;}
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAudioUrl() { return audioUrl; }
    public String getImageUrl() { return imageUrl; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
