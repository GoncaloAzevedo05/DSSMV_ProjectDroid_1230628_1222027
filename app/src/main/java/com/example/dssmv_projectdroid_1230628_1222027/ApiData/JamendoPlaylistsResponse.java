package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JamendoPlaylistsResponse {
    private JamendoResponse.Headers headers;
    private List<JamendoPlaylist> results;

    public JamendoResponse.Headers getHeaders() { return headers; }
    public List<JamendoPlaylist> getResults() { return results; }

    public static class JamendoPlaylist {
        private String id;
        private String name;
        @SerializedName("user_name")
        private String userName;
        private List<JamendoResponse.JamendoTrack> tracks;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getUserName() { return userName; }
        public List<JamendoResponse.JamendoTrack> getTracks() { return tracks; }
    }
}