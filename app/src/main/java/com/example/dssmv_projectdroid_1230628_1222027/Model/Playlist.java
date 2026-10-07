package com.example.dssmv_projectdroid_1230628_1222027.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Playlist implements Serializable{
    private String title;
    private List<Song> songs;

    public Playlist(String title) {
        this.title = title;
        this.songs = new ArrayList<>();
    }

    public String getTitle() { return title; }
    public List<Song> getSongs() { return songs; }
    public int getSongCount() { return songs.size(); }
    public void addSong(Song song) { songs.add(song); }
}
