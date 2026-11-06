package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import java.util.List;
import java.util.Map;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.AuthResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.PlaylistResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserCred;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserFavoriteResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.ProfileResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface SupaBaseInterface {

    String SUPABASE_KEY = "sb_publishable_qkQzpjqWtyLsYLLa-obcNw_sn9XdPy_";


    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body UserCred request);


    @POST("auth/v1/signup")
    Call<AuthResponse> register(@Body UserCred request);


    @POST("auth/v1/recover")
    Call<AuthResponse> forgotPassword(@Body Map<String, String> body);


    @GET("rest/v1/profiles?select=*")
    Call<List<ProfileResponse>> getUserProfile(
            @Header("Authorization") String bearerToken,
            @Query("email") String emailFilter);


    @GET("rest/v1/playlists?select=*")
    Call<List<PlaylistResponse>> getUserPlaylists(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter
    );

    @Headers({
            "Prefer: resolution=merge-duplicates"
    })
    @POST("rest/v1/playlists")
    Call<ResponseBody> syncPlaylist(
            @Header("Authorization") String bearerToken,
            @Body Object playlistData
    );


    @GET("rest/v1/user_favorites?select=*")
    Call<List<UserFavoriteResponse>> getUserFavorites(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter
    );


    @POST("rest/v1/user_favorites")
    Call<ResponseBody> addFavorite(
            @Header("Authorization") String bearerToken,
            @Body Object favoriteData
    );

    @DELETE("rest/v1/user_favorites")
    Call<ResponseBody> removeFavorite(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("song_id") String songIdFilter
    );

}
