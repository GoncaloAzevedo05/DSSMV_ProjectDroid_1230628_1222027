package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("created_at")
    private String createdAt;

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getCreatedAt() { return createdAt; }
}
