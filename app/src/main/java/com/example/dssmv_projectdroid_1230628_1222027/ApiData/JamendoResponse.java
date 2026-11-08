package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JamendoResponse {
    private Headers headers;
    private List<JamendoTrack> results;

    public Headers getHeaders() { return headers; }
    public List<JamendoTrack> getResults() { return results; }

    public static class Headers {
        private String status;
        private int code;
        @SerializedName("error_message")
        private String errorMessage;

        public String getStatus() { return status; }
        public int getCode() { return code; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static class JamendoTrack {
        private String id;
        private String name;
        @SerializedName("artist_name")
        private String artistName;
        @SerializedName("album_name")
        private String albumName;
        private String image;
        private String audio;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getArtistName() { return artistName; }
        public String getAlbumName() { return albumName; }
        public String getImage() { return image; }
        public String getAudio() { return audio; }
    }
}