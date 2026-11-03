package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class AuthResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("user")
    private UserData user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserData getUser() {
        return user;
    }

    public static class UserData {

        @SerializedName("id")
        private String id;

        @SerializedName("email")
        private String email;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("user_metadata")
        private Map<String, Object> userMetadata;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public Map<String, Object> getUserMetadata() {
            return userMetadata;
        }

        public String getUsername() {
            if (userMetadata != null) {
                if (userMetadata.containsKey("display_name") && userMetadata.get("display_name") != null) {
                    return userMetadata.get("display_name").toString();
                }
                if (userMetadata.containsKey("full_name") && userMetadata.get("full_name") != null) {
                    return userMetadata.get("full_name").toString();
                }
                if (userMetadata.containsKey("username") && userMetadata.get("username") != null) {
                    return userMetadata.get("username").toString();
                }
            }
            return email != null ? email.split("@")[0] : "";
        }
    }
}