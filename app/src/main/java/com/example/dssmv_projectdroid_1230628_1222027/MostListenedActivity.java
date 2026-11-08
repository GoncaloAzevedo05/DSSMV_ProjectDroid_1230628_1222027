package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dssmv_projectdroid_1230628_1222027.Adapters.PodiumAdapter; // <-- Usar o PodiumAdapter
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MostListenedActivity extends AppCompatActivity {

    private RecyclerView recyclerMostListened;
    private PodiumAdapter podiumAdapter; // <-- Alterado para PodiumAdapter
    private List<Song> topSongsList;
    private SharedPreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_most_listened);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);
        recyclerMostListened = findViewById(R.id.recyclerMostListened);

        topSongsList = loadTop3MostListened();

        podiumAdapter = new PodiumAdapter(topSongsList, (song, list, position) -> {
            Intent intent = new Intent(MostListenedActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        if (recyclerMostListened != null) {
            recyclerMostListened.setLayoutManager(new LinearLayoutManager(this));
            recyclerMostListened.setAdapter(podiumAdapter);
        }

        if (topSongsList.isEmpty()) {
            Toast.makeText(this, "Ainda não tens músicas ouvidas registadas.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Song> loadTop3MostListened() {
        List<Song> allSongs = new ArrayList<>();
        String userEmail = prefsManager.getUserEmail();

        List<Song> favorites = prefsManager.loadFavoriteSongs(userEmail);
        if (favorites != null) allSongs.addAll(favorites);

        List<Playlist> downloaded = prefsManager.loadDownloadedPlaylists(userEmail);
        if (downloaded != null) {
            for (Playlist p : downloaded) {
                if (p.getSongs() != null) allSongs.addAll(p.getSongs());
            }
        }

        allSongs.add(new Song("101", "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"));
        allSongs.add(new Song("102", "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"));
        allSongs.add(new Song("103", "Save Your Tears", "The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300"));

        Collections.sort(allSongs, (s1, s2) -> {
            int count1 = prefsManager.getPlayCount(s1.getId());
            int count2 = prefsManager.getPlayCount(s2.getId());
            return Integer.compare(count2, count1);
        });

        List<Song> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allSongs.size()); i++) {
            top3.add(allSongs.get(i));
        }

        return top3;
    }
}