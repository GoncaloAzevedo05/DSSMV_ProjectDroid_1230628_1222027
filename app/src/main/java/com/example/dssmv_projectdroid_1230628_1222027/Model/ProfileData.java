package com.example.dssmv_projectdroid_1230628_1222027.Model;

public class ProfileData {
    private String name;
    private String email;
    private String profilePictureUrl;
    private String createdAt;

    public ProfileData(String name, String email, String profilePictureUrl) {
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public ProfileData(String name, String email, String profilePictureUrl, String createdAt) {
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getCreatedAt() { return createdAt; }
}