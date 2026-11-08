package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupabaseStorage;
import com.google.android.material.button.MaterialButton;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.PlaylistReq;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class DownloadedSongsActivity extends AppCompatActivity {
    private RecyclerView recyclerDownloadedSongs;
    private SongAdapter songAdapter;
    private List<Song> downloadedSongsList;
    private SharedPreferencesManager prefsManager;
    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_downloaded_songs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_downloaded), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);
        recyclerDownloadedSongs = findViewById(R.id.recyclerDownloadedSongs);
        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnAddLocalSong = findViewById(R.id.btnAddLocalSong);

        btnBack.setOnClickListener(v -> finish());

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) return;

                    String displayName = "Música Local";
                    try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                            if (nameIndex != -1) {
                                displayName = cursor.getString(nameIndex);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    long size = SupabaseStorage.getFileSize(this, uri);
                    if (size > SupabaseStorage.MAX_BYTES) {
                        Toast.makeText(this, "O ficheiro tem mais de 50 MB e não pode ser enviado.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String ext = ".mp3";
                    int dot = displayName.lastIndexOf('.');
                    if (dot != -1) {
                        String extCandidate = displayName.substring(dot).toLowerCase(java.util.Locale.ROOT);
                        if (extCandidate.matches("\\.[a-z0-9]{1,5}")) {
                            ext = extCandidate;
                        }
                    }

                    String userEmail = prefsManager.getUserEmail();
                    String userFolder = userEmail != null ? userEmail.replaceAll("[^a-zA-Z0-9]", "_") : "anon";
                    String objectName = userFolder + "/" + System.currentTimeMillis() + ext;

                    final String title = displayName;
                    Toast.makeText(this, "A enviar música para a nuvem...", Toast.LENGTH_SHORT).show();

                    SupabaseStorage.uploadAudio(this, uri, objectName, new SupabaseStorage.UploadCallback() {
                        @Override
                        public void onSuccess(String publicUrl) {
                            runOnUiThread(() -> {
                                Song newSong = new Song(
                                        "local_" + System.currentTimeMillis(),
                                        title,
                                        "Carregada por mim",
                                        publicUrl
                                );
                                saveLocalSongToDownloads(newSong);
                                loadDownloadedSongs();
                                Toast.makeText(DownloadedSongsActivity.this,
                                        "Música importada com sucesso!", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            android.util.Log.e("UPLOAD_ERR", message);
                            runOnUiThread(() -> Toast.makeText(DownloadedSongsActivity.this,
                                    "Falha no upload: " + message, Toast.LENGTH_LONG).show());
                        }
                    });
                }
        );

        btnAddLocalSong.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"audio/*"});
        });

        loadDownloadedSongs();
    }

    private void loadDownloadedSongs() {
        downloadedSongsList = new ArrayList<>();
        String userEmail = prefsManager.getUserEmail();

        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists(userEmail);
        for (Playlist playlist : downloadedPlaylists) {
            if (playlist.getSongs() != null) {
                downloadedSongsList.addAll(playlist.getSongs());
            }
        }


        songAdapter = new SongAdapter(downloadedSongsList,
                (song, list, position) -> {
                    Intent intent = new Intent(DownloadedSongsActivity.this, MusicPlayerActivity.class);
                    intent.putExtra("SONG_KEY", song);
                    intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
                    intent.putExtra("CURRENT_INDEX", position);
                    startActivity(intent);
                },
                (song, position) -> confirmRemove(song));

        recyclerDownloadedSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerDownloadedSongs.setAdapter(songAdapter);
    }

    private void saveLocalSongToDownloads(Song song) {
        String userEmail = prefsManager.getUserEmail();
        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists(userEmail);

        Playlist localPlaylist = null;
        for (Playlist p : downloadedPlaylists) {
            if (p.getTitle().equals("Local Downloads")) {
                localPlaylist = p;
                break;
            }
        }

        if (localPlaylist == null) {
            localPlaylist = new Playlist("Local Downloads");
            localPlaylist.setDownloaded(true);
            downloadedPlaylists.add(localPlaylist);
        }

        if (localPlaylist.getSongs() == null) {
            localPlaylist.setSongs(new ArrayList<>());
        }

        localPlaylist.getSongs().add(song);
        prefsManager.saveDownloadedPlaylists(userEmail, downloadedPlaylists);
        syncPlaylistToCloud(userEmail, localPlaylist);
    }
    private void confirmRemove(Song song) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Remover música")
                .setMessage("Remover \"" + song.getTitle() + "\" dos downloads?")
                .setPositiveButton("Remover", (d, w) -> removeFromDownloads(song))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void removeFromDownloads(Song song) {
        String userEmail = prefsManager.getUserEmail();
        List<Playlist> playlists = prefsManager.loadDownloadedPlaylists(userEmail);

        for (Playlist p : playlists) {
            if (p.getSongs() != null) {
                p.getSongs().removeIf(s -> s.getId() != null && s.getId().equals(song.getId()));
            }
        }

        prefsManager.saveDownloadedPlaylists(userEmail, playlists);
        loadDownloadedSongs();
    }

    private void syncPlaylistToCloud(String userEmail, Playlist playlist) {
        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);
        PlaylistReq req = new PlaylistReq(
                userEmail,
                playlist.getTitle(),
                playlist.getImageUrl(),
                playlist.isDownloaded(),
                playlist.getSongs()
        );

        api.syncPlaylist("Bearer " + SupaBaseClient.getApiKey(), req)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            try {
                                String err = response.errorBody() != null ? response.errorBody().string() : "";
                                android.util.Log.e("SYNC_ERR", "HTTP " + response.code() + " " + err);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        android.util.Log.e("SYNC_ERR", "Falha de rede: " + t.getMessage());
                    }
                });
    }
}