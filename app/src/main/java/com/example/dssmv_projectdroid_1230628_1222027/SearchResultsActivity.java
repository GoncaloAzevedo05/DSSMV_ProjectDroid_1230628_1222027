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

        String query = getIntent().getStringExtra("EXTRA_QUERY");
        String filterType = getIntent().getStringExtra("EXTRA_FILTER");

        if (query != null) {
            txtSearchTitle.setText("Resultados para: \"" + query + "\"");
            performSearch(query);
        }

        songAdapter = new SongAdapter(songResults, song -> {
            Intent intent = new Intent(SearchResultsActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            startActivity(intent);
        });

        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerResults.setAdapter(songAdapter);
    }

    private void performSearch(String query) {
        songResults = new ArrayList<>();
        List<Song> mockDatabase = new ArrayList<>();
        mockDatabase.add(new Song("Blinding Lights", "The Weeknd", "", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"));
        mockDatabase.add(new Song("Starboy", "The Weeknd", "", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"));
        mockDatabase.add(new Song("Save Your Tears", "The Weeknd", "", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300"));
        mockDatabase.add(new Song("Shape of You", "Ed Sheeran", "", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"));
        mockDatabase.add(new Song("Bohemian Rhapsody", "Queen", "", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"));

        String searchQuery = query.toLowerCase().trim();
        for (Song song : mockDatabase) {
            if (song.getTitle().toLowerCase().contains(searchQuery) ||
                    song.getArtist().toLowerCase().contains(searchQuery)) {
                songResults.add(song);
            }
        }

        if (songResults.isEmpty()) {
            Toast.makeText(this, "Nenhum resultado encontrado.", Toast.LENGTH_SHORT).show();
        }
    }
}
