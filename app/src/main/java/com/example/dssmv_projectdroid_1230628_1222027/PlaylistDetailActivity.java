package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {
    private ImageView imgPlaylistCover;
    private TextView txtPlaylistName, txtPlaylistCreator, txtToolbarTitle;
    private Button btnPlaylistAction;
    private RecyclerView recyclerPlaylistSongs;

    private Playlist playlist;
    private boolean isAlreadyDownloadedOrMine;
    private SharedPreferencesManager prefsManager;
    private SongAdapter songAdapter;

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
        recyclerPlaylistSongs = findViewById(R.id.recyclerPlaylistSongs);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        playlist = (Playlist) getIntent().getSerializableExtra("EXTRA_PLAYLIST");
        isAlreadyDownloadedOrMine = getIntent().getBooleanExtra("EXTRA_IS_SAVED", false);

        if (playlist != null) {
            populateUI();
        }

        if (isAlreadyDownloadedOrMine) {
            btnPlaylistAction.setText("Delete");
            btnPlaylistAction.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E91429")));
            btnPlaylistAction.setOnClickListener(v -> deletePlaylistAndFinish());
        } else {
            btnPlaylistAction.setText("Add");
            btnPlaylistAction.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1DB954")));
            btnPlaylistAction.setOnClickListener(v -> saveToDownloadedAndFinish());
        }
    }
    private void populateUI() {
        txtPlaylistName.setText(playlist.getTitle());
        txtToolbarTitle.setText(playlist.getTitle());
        txtPlaylistCreator.setText("Criado por: SoundDroid");

        if (playlist.getImageUrl() != null && !playlist.getImageUrl().isEmpty()) {
            Glide.with(this).load(playlist.getImageUrl()).into(imgPlaylistCover);
        }

        songAdapter = new SongAdapter(playlist.getSongs(), (song, list, position) -> {
            Intent intent = new Intent(PlaylistDetailActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        recyclerPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlaylistSongs.setAdapter(songAdapter);
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
            downloaded.removeIf(p -> p.getId().equals(playlist.getId()));
            prefsManager.saveDownloadedPlaylists(userEmail, downloaded);
        } else {
            List<Playlist> myLists = prefsManager.loadMyPlaylists(userEmail);
            myLists.removeIf(p -> p.getId().equals(playlist.getId()));
            prefsManager.saveMyPlaylists(userEmail, myLists);
        }
        Toast.makeText(this, "Playlist removida.", Toast.LENGTH_SHORT).show();
        finish();
    }
}