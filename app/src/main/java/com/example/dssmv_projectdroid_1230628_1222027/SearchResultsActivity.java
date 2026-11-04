package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView txtSearchTitle;
    private RecyclerView recyclerResults;
    private SongAdapter songAdapter;
    private List<Song> songResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search_results), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtSearchTitle = findViewById(R.id.txtSearchTitle);
        recyclerResults = findViewById(R.id.recyclerResults);

        songResults = new ArrayList<>();

        songAdapter = new SongAdapter(songResults, (song, list, position) -> {
            Intent intent = new Intent(SearchResultsActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerResults.setAdapter(songAdapter);

        String query = getIntent().getStringExtra("EXTRA_QUERY");
        String filterType = getIntent().getStringExtra("EXTRA_FILTER");

        if (query != null) {
            txtSearchTitle.setText("Resultados para: \"" + query + "\"");
            performSearch(query);
        }
    }

    private void performSearch(String query) {
        songResults = new ArrayList<>();

        List<Song> localMatches = searchLocalSongs(query);
        if (localMatches != null && !localMatches.isEmpty()) {
            songResults.addAll(localMatches);
        }

        List<Song> mockDatabase = new ArrayList<>();
        mockDatabase.add(new Song("101", "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"));
        mockDatabase.add(new Song("102", "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"));
        mockDatabase.add(new Song("103", "Save Your Tears", "The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300"));
        mockDatabase.add(new Song("104", "Shape of You", "Ed Sheeran", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"));
        mockDatabase.add(new Song("105", "Bohemian Rhapsody", "Queen", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"));

        String searchQuery = query.toLowerCase().trim();
        for (Song song : mockDatabase) {
            String title = song.getTitle() != null ? song.getTitle().toLowerCase() : "";
            String artist = song.getArtist() != null ? song.getArtist().toLowerCase() : "";

            if (title.contains(searchQuery) || artist.contains(searchQuery)) {
                boolean exists = false;
                for (Song s : songResults) {
                    if (s.getId() != null && s.getId().equals(song.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    songResults.add(song);
                }
            }
        }

        if (songResults.isEmpty()) {
            Toast.makeText(this, "Nenhum resultado encontrado.", Toast.LENGTH_SHORT).show();
        }

        if (songAdapter != null) {
            songAdapter.setSongs(songResults);
        }
    }

    private List<Song> searchLocalSongs(String query) {
        List<Song> matchingSongs = new ArrayList<>();
        if (query == null) return matchingSongs;

        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists();

        if (downloadedPlaylists != null) {
            for (Playlist playlist : downloadedPlaylists) {
                if (playlist != null && playlist.getSongs() != null) {
                    for (Song song : playlist.getSongs()) {
                        if (song != null) {
                            String title = song.getTitle() != null ? song.getTitle().toLowerCase() : "";
                            String artist = song.getArtist() != null ? song.getArtist().toLowerCase() : "";

                            if (title.contains(query.toLowerCase()) || artist.contains(query.toLowerCase())) {
                                boolean exists = false;
                                for (Song s : matchingSongs) {
                                    if (s.getId() != null && s.getId().equals(song.getId())) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
                                    matchingSongs.add(song);
                                }
                            }
                        }
                    }
                }
            }
        }
        return matchingSongs;
    }
}