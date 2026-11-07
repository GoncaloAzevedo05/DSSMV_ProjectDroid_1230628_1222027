package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;

public class UserFavoriteReq {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("song_id")
    private String songId;

    @SerializedName("song_title")
    private String songTitle;

    @SerializedName("artist_name")
    private String artistName;

    @SerializedName("audio_url")
    private String audioUrl;

    @SerializedName("image_url")
    private String imageUrl;

    public UserFavoriteReq(String userId, String songId, String songTitle, String artistName, String audioUrl, String imageUrl) {
        this.userId = userId;
        this.songId = songId;
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
    }
    public String getUserId() { return userId; }
    public String getSongId() { return songId; }
    public String getSongTitle() { return songTitle; }
    public String getArtistName() { return artistName; }
    public String getAudioUrl() { return audioUrl; }
    public String getImageUrl() { return imageUrl; }
}
