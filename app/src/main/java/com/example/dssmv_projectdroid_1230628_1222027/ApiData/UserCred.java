package com.example.dssmv_projectdroid_1230628_1222027.ApiData;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UserCred {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("data")
    private Map<String, Object> data;

    public UserCred(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserCred(String email, String password, Map<String, Object> data) {
        this.email = email;
        this.password = password;
        this.data = data;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getData() {
        return data;
    }
}