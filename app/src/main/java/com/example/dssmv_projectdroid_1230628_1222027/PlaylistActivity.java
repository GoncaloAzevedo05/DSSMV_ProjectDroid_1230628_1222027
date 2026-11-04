package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Adapters.PlaylistAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private RecyclerView recyclerMyPlaylists;
    private RecyclerView recyclerDownloadedPlaylists;
    private ExtendedFloatingActionButton fabAddPlaylist;
    private List<Playlist> myPlaylistsList;
    private List<Playlist> downloadedPlaylistsList;
    private PlaylistAdapter myPlaylistsAdapter;
    private PlaylistAdapter downloadedPlaylistsAdapter;
    private SharedPreferencesManager prefsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_playlist), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);

        recyclerMyPlaylists = findViewById(R.id.recyclerMyPlaylists);
        recyclerDownloadedPlaylists = findViewById(R.id.recyclerDownloadedPlaylists);
        fabAddPlaylist = findViewById(R.id.fabAddPlaylist);

        recyclerMyPlaylists.setLayoutManager(new LinearLayoutManager(this));
        recyclerDownloadedPlaylists.setLayoutManager(new LinearLayoutManager(this));

        loadPlaylistsData();

        fabAddPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaylistsData();
    }

    private void loadPlaylistsData() {
        String userEmail = prefsManager.getUserEmail();

        myPlaylistsList = prefsManager.loadMyPlaylists();
        downloadedPlaylistsList = prefsManager.loadDownloadedPlaylists(userEmail);

        if (myPlaylistsList.isEmpty() && downloadedPlaylistsList.isEmpty()) {
            myPlaylistsList.add(new Playlist("Favorites"));
            prefsManager.saveMyPlaylists(myPlaylistsList);

            Playlist downloadedMock = new Playlist("1", "Álbum Offline (API)", "", true);
            downloadedPlaylistsList.add(downloadedMock);
            prefsManager.saveDownloadedPlaylists(userEmail,downloadedPlaylistsList);
        }

        myPlaylistsAdapter = new PlaylistAdapter(myPlaylistsList, new PlaylistAdapter.OnPlaylistListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {
                openDetail(playlist, true);
            }

            @Override
            public void onPlaylistLongClick(Playlist playlist, int position) {
                showOptionsDialog(playlist, position, true);
            }
        });
        recyclerMyPlaylists.setAdapter(myPlaylistsAdapter);

        downloadedPlaylistsAdapter = new PlaylistAdapter(downloadedPlaylistsList, new PlaylistAdapter.OnPlaylistListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {
                openDetail(playlist, true);
            }

            @Override
            public void onPlaylistLongClick(Playlist playlist, int position) {
                showOptionsDialog(playlist, position, false);
            }
        });
        recyclerDownloadedPlaylists.setAdapter(downloadedPlaylistsAdapter);
    }

    private void openDetail(Playlist playlist, boolean isSaved) {
        Intent intent = new Intent(PlaylistActivity.this, PlaylistDetailActivity.class);
        intent.putExtra("EXTRA_PLAYLIST", playlist);
        intent.putExtra("EXTRA_IS_SAVED", isSaved);
        startActivity(intent);
    }

    private void showOptionsDialog(Playlist playlist, int position, boolean isMyPlaylist) {
        String[] options = {"View", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(playlist.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openDetail(playlist, true);
                    } else {
                        confirmAndDelete(position, isMyPlaylist);
                    }
                })
                .show();
    }

    private void confirmAndDelete(int position, boolean isMyPlaylist) {
        String userEmail = prefsManager.getUserEmail();
        List<Playlist> targetList = isMyPlaylist ? myPlaylistsList : downloadedPlaylistsList;
        Playlist playlistToDelete = targetList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Playlist")
                .setMessage("Are you sure you want to delete \"" + playlistToDelete.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    targetList.remove(position);

                    if (isMyPlaylist) {
                        prefsManager.saveMyPlaylists(myPlaylistsList);
                        myPlaylistsAdapter.notifyItemRemoved(position);
                        myPlaylistsAdapter.notifyItemRangeChanged(position, myPlaylistsList.size());
                    } else {
                        prefsManager.saveDownloadedPlaylists(userEmail,downloadedPlaylistsList);
                        downloadedPlaylistsAdapter.notifyItemRemoved(position);
                        downloadedPlaylistsAdapter.notifyItemRangeChanged(position, downloadedPlaylistsList.size());
                    }

                    Toast.makeText(this, "Playlist apagada.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showCreatePlaylistDialog() {
        EditText input = new EditText(this);
        input.setHint("Playlist Name");

        new AlertDialog.Builder(this)
                .setTitle("Create Playlist")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Playlist newPlaylist = new Playlist(name);
                        myPlaylistsList.add(newPlaylist);

                        prefsManager.saveMyPlaylists(myPlaylistsList);

                        myPlaylistsAdapter.notifyItemInserted(myPlaylistsList.size() - 1);
                        Toast.makeText(this, "Playlist criada!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}