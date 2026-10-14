package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;

import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private RecyclerView recyclerPlaylists;
    private Button btnAddPlaylist;
    private List<Playlist> playlists;
    private PlaylistAdapter adapter;
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
        playlists = prefsManager.loadPlaylists();

        recyclerPlaylists = findViewById(R.id.recyclerPlaylists);
        btnAddPlaylist = findViewById(R.id.btnAddPlaylist);
        adapter = new PlaylistAdapter(playlists, position -> showDeleteConfirmationDialog(position));

        recyclerPlaylists.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlaylists.setAdapter(adapter);

        btnAddPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());}
    private void showCreatePlaylistDialog() {
        EditText input = new EditText(this);
        input.setHint("Playlist Name");

        new AlertDialog.Builder(this)
                .setTitle("Create Playlist")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        playlists.add(new Playlist(name));
                        adapter.notifyItemInserted(playlists.size() - 1);
                        prefsManager.savePlaylists(playlists);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showDeleteConfirmationDialog(int position) {
        Playlist playlistToDelete = playlists.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Playlist")
                .setMessage("Are you sure you want to delete \"" + playlistToDelete.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    playlists.remove(position);
                    adapter.notifyItemRemoved(position);

                    prefsManager.savePlaylists(playlists);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}