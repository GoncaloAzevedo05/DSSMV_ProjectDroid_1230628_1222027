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
import com.google.android.material.button.MaterialButton;

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
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        String displayName = "Música Local";
                        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                                if (nameIndex != -1) {
                                    displayName = cursor.getString(nameIndex);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Song newSong = new Song(
                                "local_" + System.currentTimeMillis(),
                                displayName,
                                "Armazenamento Local",
                                uri.toString()
                        );

                        saveLocalSongToDownloads(newSong);
                        loadDownloadedSongs();
                        Toast.makeText(this, "Música importada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnAddLocalSong.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"audio/*"});
        });

        loadDownloadedSongs();
    }

    private void loadDownloadedSongs() {
        downloadedSongsList = new ArrayList<>();

        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists();
        for (Playlist playlist : downloadedPlaylists) {
            if (playlist.getSongs() != null) {
                downloadedSongsList.addAll(playlist.getSongs());
            }
        }

        songAdapter = new SongAdapter(downloadedSongsList, (song, list, position) -> {
            Intent intent = new Intent(DownloadedSongsActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        recyclerDownloadedSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerDownloadedSongs.setAdapter(songAdapter);
    }

    private void saveLocalSongToDownloads(Song song) {
        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists();

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

        localPlaylist.getSongs().add(song);
        prefsManager.saveDownloadedPlaylists(downloadedPlaylists);
    }
}