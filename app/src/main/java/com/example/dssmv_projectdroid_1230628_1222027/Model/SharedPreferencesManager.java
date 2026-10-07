package com.example.dssmv_projectdroid_1230628_1222027.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class SharedPreferencesManager {
    private static final String PREFS_NAME = "MusicAppPrefs";
    private static final String KEY_PLAYLISTS = "playlists_list";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void savePlaylists(List<Playlist> playlists) {
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(KEY_PLAYLISTS, json).apply();
    }

    public List<Playlist> loadPlaylists() {
        String json = sharedPreferences.getString(KEY_PLAYLISTS, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        List<Playlist> savedList = gson.fromJson(json, type);

        return savedList != null ? savedList : new ArrayList<>();
    }
}
