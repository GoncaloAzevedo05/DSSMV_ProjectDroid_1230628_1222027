package com.example.dssmv_projectdroid_1230628_1222027.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Playlist implements Serializable{
    private String id;
    private String title;
    private String imageUrl;
    private boolean isDownloaded;
    private List<Song> songs;

    public Playlist(String id, String title, String imageUrl, boolean isDownloaded) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.isDownloaded = isDownloaded;
        this.songs = new ArrayList<>();
    }

    public Playlist(String title) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.imageUrl = "";
        this.isDownloaded = false;
        this.songs = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public boolean isDownloaded() { return isDownloaded; }
    public List<Song> getSongs() {
        if (this.songs == null) {
            this.songs = new ArrayList<>();
        }
        return this.songs;
    }



    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }
    public void setSongs(List<Song> songs) {
        if (songs == null) {
            this.songs = new ArrayList<>();
        } else {
            this.songs = songs;
        }
    }

    public int getSongCount() { return songs.size(); }
    public void addSong(Song song) {
        if (this.songs == null) {
            this.songs = new ArrayList<>();
        }
        this.songs.add(song);
    }
}
