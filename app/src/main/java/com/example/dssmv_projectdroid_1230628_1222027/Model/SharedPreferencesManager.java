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
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_CREATED_AT = "user_created_at";
    private static final String KEY_LOGIN_TIMESTAMP = "login_timestamp";

    private static final long SESSION_EXPIRATION_TIME = 86400000L;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveUserSession(String email, String name, String createdAt) {
        sharedPreferences.edit()
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_CREATED_AT, createdAt)
                .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public boolean isUserLoggedIn() {
        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIMESTAMP, 0);
        if (loginTime == 0) return false;

        long currentTime = System.currentTimeMillis();
        boolean isValid = (currentTime - loginTime) < SESSION_EXPIRATION_TIME;

        if (!isValid) {
            clearUserSession();
        }
        return isValid;
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserCreatedAt() {
        return sharedPreferences.getString(KEY_USER_CREATED_AT, "");
    }

    public void clearUserSession() {
        sharedPreferences.edit()
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_CREATED_AT)
                .remove(KEY_LOGIN_TIMESTAMP)
                .apply();
    }

    public void saveMyPlaylists(String userEmail, List<Playlist> playlists) {
        if (userEmail == null || userEmail.isEmpty()) return;
        String key = KEY_MY_PLAYLISTS + "_" + userEmail;
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public List<Playlist> loadMyPlaylists(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) return new ArrayList<>();
        String key = KEY_MY_PLAYLISTS + "_" + userEmail;
        String json = sharedPreferences.getString(key, null);
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        List<Playlist> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void saveDownloadedPlaylists(String userEmail,List<Playlist> playlists) {
        if (userEmail == null || userEmail.isEmpty()) return;
        String key = KEY_DOWNLOADED_PLAYLISTS + "_" + userEmail;
        String json = gson.toJson(playlists);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public List<Playlist> loadDownloadedPlaylists(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) return new ArrayList<>();
        String key = KEY_DOWNLOADED_PLAYLISTS + "_" + userEmail;
        String json = sharedPreferences.getString(key, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        List<Playlist> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
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

    public void saveFavoriteSongs(String userEmail, List<Song> songs) {
        if (userEmail == null || userEmail.isEmpty()) return;
        String key = "favorite_songs_" + userEmail;
        String json = new com.google.gson.Gson().toJson(songs);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public List<Song> loadFavoriteSongs(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) return new ArrayList<>();
        String key = "favorite_songs_" + userEmail;
        String json = sharedPreferences.getString(key, null);
        if (json == null) {
            return new java.util.ArrayList<>();
        }
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Song>>() {}.getType();
        return new com.google.gson.Gson().fromJson(json, type);
    }

    public void incrementPlayCount(String songId) {
        int currentCount = getPlayCount(songId);
        sharedPreferences.edit().putInt("play_count_" + songId, currentCount + 1).apply();
    }

    public int getPlayCount(String songId) {
        return sharedPreferences.getInt("play_count_" + songId, 0);
    }

    public void saveDarkModeState(boolean isEnabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isEnabled).apply();
    }

    public boolean loadDarkModeState() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
}
