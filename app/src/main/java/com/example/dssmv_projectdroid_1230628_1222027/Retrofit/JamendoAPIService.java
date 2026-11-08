package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.JamendoPlaylistsResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.JamendoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamendoAPIService {
    @GET("tracks/")
    Call<JamendoResponse> searchTracks(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("imagesize") int imageSize,
            @Query("namesearch") String trackName,
            @Query("search") String search,
            @Query("artist_name") String artistName,
            @Query("album_name") String albumName
    );

    @GET("playlists/")
    Call<JamendoPlaylistsResponse> searchPlaylists(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("namesearch") String name
    );

    @GET("playlists/tracks/")
    Call<JamendoPlaylistsResponse> getPlaylistTracks(
            @Query("client_id") String clientId,
            @Query("format") String format,
            @Query("id") String playlistId
    );
}