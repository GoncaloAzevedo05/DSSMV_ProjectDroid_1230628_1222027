package com.example.dssmv_projectdroid_1230628_1222027;

import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistActivity extends AppCompatActivity {

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

        RecyclerView recyclerPlaylists = findViewById(R.id.recyclerPlaylists);
        Button btnAddPlaylist = findViewById(R.id.btnAddPlaylist);

        recyclerPlaylists.setLayoutManager(new LinearLayoutManager(this));

        btnAddPlaylist.setOnClickListener(v -> {
            Toast.makeText(PlaylistActivity.this, "New funciona", Toast.LENGTH_SHORT).show();
        });
    }
}