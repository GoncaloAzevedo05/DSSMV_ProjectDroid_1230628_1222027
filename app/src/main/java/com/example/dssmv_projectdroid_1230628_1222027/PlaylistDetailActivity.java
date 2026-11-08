package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.PlaylistReq;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    private ImageView imgPlaylistCover;
    private TextView txtPlaylistName, txtPlaylistCreator, txtToolbarTitle;
    private Button btnPlaylistAction, btnAddSong;
    private RecyclerView recyclerPlaylistSongs;

    private Playlist playlist;
    private boolean isAlreadyDownloadedOrMine;
    private SharedPreferencesManager prefsManager;
    private SongAdapter songAdapter;
    private ActivityResultLauncher<Intent> searchSongLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_playlist_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);

        imgPlaylistCover = findViewById(R.id.imgPlaylistCover);
        txtPlaylistName = findViewById(R.id.txtPlaylistName);
        txtPlaylistCreator = findViewById(R.id.txtPlaylistCreator);
        txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
        btnPlaylistAction = findViewById(R.id.btnPlaylistAction);
        btnAddSong = findViewById(R.id.btnAddSong);

        recyclerPlaylistSongs = findViewById(R.id.recyclerPlaylistSongs);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        playlist = (Playlist) getIntent().getSerializableExtra("EXTRA_PLAYLIST");
        isAlreadyDownloadedOrMine = getIntent().getBooleanExtra("EXTRA_IS_SAVED", false);

        if (playlist != null) {
            populateUI();
        }

        searchSongLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Song selectedSong = (Song) result.getData().getSerializableExtra("SELECTED_SONG");
                        if (selectedSong != null) {
                            addSongToThisPlaylist(selectedSong);
                        }
                    }
                }
        );

        if (isAlreadyDownloadedOrMine) {
            btnPlaylistAction.setText("Delete");
            btnPlaylistAction.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E91429")));
            btnPlaylistAction.setOnClickListener(v -> deletePlaylistAndFinish());

            if (!playlist.isDownloaded()) {
                btnAddSong.setVisibility(View.VISIBLE);
                btnAddSong.setOnClickListener(v -> {
                    Intent intent = new Intent(PlaylistDetailActivity.this, SearchActivity.class);
                    intent.putExtra("IS_SELECTION_MODE", true); // Modo de seleção
                    searchSongLauncher.launch(intent);
                });
            } else {
                btnAddSong.setVisibility(View.GONE);
            }
        } else {
            btnPlaylistAction.setText("Add");
            btnPlaylistAction.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1DB954")));
            btnPlaylistAction.setOnClickListener(v -> saveToDownloadedAndFinish());
            btnAddSong.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userEmail = prefsManager.getUserEmail();

        if (playlist.isDownloaded()) {
            List<Playlist> downloaded = prefsManager.loadDownloadedPlaylists(userEmail);
            for (Playlist p : downloaded) {
                if (p.getTitle().equals(playlist.getTitle())) {
                    playlist = p;
                    break;
                }
            }
        } else {
            List<Playlist> myLists = prefsManager.loadMyPlaylists(userEmail);
            for (Playlist p : myLists) {
                if (p.getTitle().equals(playlist.getTitle())) {
                    playlist = p;
                    break;
                }
            }
        }

        if (songAdapter != null && playlist.getSongs() != null) {
            songAdapter.setSongs(playlist.getSongs());
        }
    }

    private void populateUI() {
        txtPlaylistName.setText(playlist.getTitle());
        txtToolbarTitle.setText(playlist.getTitle());
        txtPlaylistCreator.setText("Criado por: SoundDroid");

        if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
            Glide.with(this).load(playlist.getImageUrl()).into(imgPlaylistCover);
        }

        if (playlist.getSongs() == null) {
            playlist.setSongs(new ArrayList<>());
        }

        songAdapter = new SongAdapter(playlist.getSongs(), (song, list, position) -> {
            Intent intent = new Intent(PlaylistDetailActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        if (isAlreadyDownloadedOrMine) {
            songAdapter.setOnRemoveListener((song, position) -> removeSongFromThisPlaylist(song));
        }

        recyclerPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlaylistSongs.setAdapter(songAdapter);
    }

    private void addSongToThisPlaylist(Song song) {
        if (playlist.getSongs() == null) {
            playlist.setSongs(new ArrayList<>());
        }

        for (Song s : playlist.getSongs()) {
            if (s.getId().equals(song.getId())) {
                Toast.makeText(this, "A música já faz parte desta playlist.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        playlist.getSongs().add(song);
        songAdapter.notifyDataSetChanged();

        saveCurrentPlaylistLocally();
        syncPlaylistToCloud("Música adicionada e sincronizada!");
    }

    private void removeSongFromThisPlaylist(Song song) {
        if (playlist.getSongs() == null) return;

        playlist.getSongs().removeIf(s -> s.getId() != null && s.getId().equals(song.getId()));
        songAdapter.setSongs(playlist.getSongs());

        saveCurrentPlaylistLocally();
        Toast.makeText(this, "Música removida.", Toast.LENGTH_SHORT).show();
        syncPlaylistToCloud(null);
    }

    // Grava no telemóvel a playlist atual (das tuas ou das downloaded)
    private void saveCurrentPlaylistLocally() {
        String userEmail = prefsManager.getUserEmail();

        if (playlist.isDownloaded()) {
            List<Playlist> downloaded = prefsManager.loadDownloadedPlaylists(userEmail);
            for (int i = 0; i < downloaded.size(); i++) {
                if (downloaded.get(i).getTitle().equals(playlist.getTitle())) {
                    downloaded.set(i, playlist);
                    break;
                }
            }
            prefsManager.saveDownloadedPlaylists(userEmail, downloaded);
        } else {
            List<Playlist> myLists = prefsManager.loadMyPlaylists(userEmail);
            for (int i = 0; i < myLists.size(); i++) {
                if (myLists.get(i).getTitle().equals(playlist.getTitle())) {
                    myLists.set(i, playlist);
                    break;
                }
            }
            prefsManager.saveMyPlaylists(userEmail, myLists);
        }
    }

    private void syncPlaylistToCloud(final String successMessage) {
        String userEmail = prefsManager.getUserEmail();
        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);
        PlaylistReq req = new PlaylistReq(userEmail, playlist.getTitle(), playlist.getImageUrl(), playlist.isDownloaded(), playlist.getSongs());

        api.syncPlaylist("Bearer " + SupaBaseClient.getApiKey(), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && successMessage != null) {
                    Toast.makeText(PlaylistDetailActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                } else if (!response.isSuccessful()) {
                    android.util.Log.e("SYNC_ERR", "HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this, "Guardado localmente (offline).", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToDownloadedAndFinish() {
        String userEmail = prefsManager.getUserEmail();
        List<Playlist> downloaded = prefsManager.loadDownloadedPlaylists(userEmail);
        playlist.setDownloaded(true);
        downloaded.add(playlist);
        prefsManager.saveDownloadedPlaylists(userEmail, downloaded);

        Toast.makeText(this, "Adicionado aos Downloads!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deletePlaylistAndFinish() {
        String userEmail = prefsManager.getUserEmail();
        if (playlist.isDownloaded()) {
            List<Playlist> downloaded = prefsManager.loadDownloadedPlaylists(userEmail);
            downloaded.removeIf(p -> p.getTitle().equals(playlist.getTitle()));
            prefsManager.saveDownloadedPlaylists(userEmail, downloaded);
        } else {
            List<Playlist> myLists = prefsManager.loadMyPlaylists(userEmail);
            myLists.removeIf(p -> p.getTitle().equals(playlist.getTitle()));
            prefsManager.saveMyPlaylists(userEmail, myLists);
        }
        Toast.makeText(this, "Playlist removida.", Toast.LENGTH_SHORT).show();
        finish();
    }
}