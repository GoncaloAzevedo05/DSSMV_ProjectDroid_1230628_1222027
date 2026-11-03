package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import java.util.List;
import java.util.Map;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.AuthResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserCred;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupaBaseInterface {

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body UserCred request);

    @POST("auth/v1/signup")
    Call<AuthResponse> register(@Body UserCred request);

    @POST("auth/v1/recover")
    Call<AuthResponse> forgotPassword(@Body Map<String, String> body);

    @GET("rest/v1/profiles?select=*")
    Call<ResponseBody> getUserProfile(@Query("email") String emailFilter);

}
