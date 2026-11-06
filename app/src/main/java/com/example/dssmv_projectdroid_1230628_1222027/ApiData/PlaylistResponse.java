package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlaylistResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("title")
    private String title;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("is_downloaded")
    private boolean isDownloaded;

    @SerializedName("songs")
    private List<Song> songs;

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public boolean isDownloaded() { return isDownloaded; }
    public List<Song> getSongs() { return songs; }
}