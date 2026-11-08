package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.SpotifySearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SpotifyAPIService {

    @GET("search")
    Call<SpotifySearchResponse> searchSpotify(
            @Header("X-RapidAPI-Key") String apiKey,
            @Header("X-RapidAPI-Host") String apiHost,
            @Query("q") String query,
            @Query("type") String type
    );
}
