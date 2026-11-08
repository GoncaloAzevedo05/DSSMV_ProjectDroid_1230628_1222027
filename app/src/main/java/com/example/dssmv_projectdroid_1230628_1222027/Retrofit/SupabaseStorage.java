package com.example.dssmv_projectdroid_1230628_1222027.Retrofit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class SupabaseStorage {

    private static final String BUCKET = "songs";
    public static final long MAX_BYTES = 50L * 1024 * 1024;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public interface UploadCallback {
        void onSuccess(String publicUrl);
        void onError(String message);
    }

    public static long getFileSize(Context context, Uri uri) {
        try (Cursor c = context.getContentResolver().query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int i = c.getColumnIndex(OpenableColumns.SIZE);
                if (i != -1 && !c.isNull(i)) return c.getLong(i);
            }
        } catch (Exception ignored) { }
        return -1;
    }

    public static void uploadAudio(Context context, Uri uri, String objectName, UploadCallback callback) {
        final Context appContext = context.getApplicationContext();

        String mime = appContext.getContentResolver().getType(uri);
        if (mime == null || !mime.startsWith("audio/")) mime = "audio/mpeg";
        final MediaType mediaType = MediaType.parse(mime);
        final long size = getFileSize(appContext, uri);

        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() { return mediaType; }

            @Override
            public long contentLength() { return size; }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (InputStream in = appContext.getContentResolver().openInputStream(uri)) {
                    if (in == null) throw new IOException("Não foi possível abrir o ficheiro");
                    sink.writeAll(Okio.source(in));
                }
            }
        };

        final String base = SupaBaseClient.getBaseUrl(); // termina em "/"

        Request request = new Request.Builder()
                .url(base + "storage/v1/object/" + BUCKET + "/" + objectName)
                .addHeader("apikey", SupaBaseClient.getApiKey())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response r = response) {
                    if (r.isSuccessful()) {
                        callback.onSuccess(base + "storage/v1/object/public/" + BUCKET + "/" + objectName);
                    } else {
                        String err = r.body() != null ? r.body().string() : "";
                        callback.onError("HTTP " + r.code() + " " + err);
                    }
                }
            }
        });
    }
}