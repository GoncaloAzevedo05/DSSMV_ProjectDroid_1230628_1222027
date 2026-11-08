package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupaBaseClient {

    private static Retrofit retrofit;

    private static final String BASE_URL = "https://yeagosapoqkuzioubdpf.supabase.co/";

    private static final String API_KEY = SupaBaseInterface.SUPABASE_KEY;

    public static Retrofit getClient() {
        if (retrofit == null) {

            Interceptor headerInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder()
                        .addHeader("apikey", API_KEY)
                        .addHeader("Content-Type", "application/json");

                return chain.proceed(builder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static String getApiKey() {
        return API_KEY;
    }

    public static String getBaseUrl() {return BASE_URL;}
}
