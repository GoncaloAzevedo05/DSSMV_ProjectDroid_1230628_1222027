package com.example.dssmv_projectdroid_1230628_1222027.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class SharedPreferencesManager {
    private static final String PREFS_NAME = "SoundDroidPrefs";
    private static final String KEY_MY_PLAYLISTS = "my_playlists";
    private static final String KEY_DOWNLOADED_PLAYLISTS = "downloaded_playlists";
    private static final String KEY_SEARCH_HISTORY = "search_history_list";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveMyPlaylists(List<Playlist> playlists) {
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(KEY_MY_PLAYLISTS, json).apply();
    }

    public List<Playlist> loadMyPlaylists() {
        String json = sharedPreferences.getString(KEY_MY_PLAYLISTS, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveDownloadedPlaylists(List<Playlist> playlists) {
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(KEY_DOWNLOADED_PLAYLISTS, json).apply();
    }

    public List<Playlist> loadDownloadedPlaylists() {
        String json = sharedPreferences.getString(KEY_DOWNLOADED_PLAYLISTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveSearchQuery(String query) {
        List<String> history = loadSearchHistory();
        history.remove(query);
        history.add(0, query);

        if (history.size() > 10) {
            history = history.subList(0, 10);
        }

        String json = gson.toJson(history);
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, json).apply();
    }

    public List<String> loadSearchHistory() {
        String json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> savedList = gson.fromJson(json, type);
        return savedList != null ? savedList : new ArrayList<>();
    }
}
