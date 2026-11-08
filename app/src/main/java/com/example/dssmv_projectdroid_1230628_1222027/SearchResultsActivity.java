package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
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
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.SpotifySearchResponse;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.RetrofitClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SpotifyAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private TextView txtSearchTitle;
    private RecyclerView recyclerResults;
    private SongAdapter songAdapter;
    private List<Song> songResults;

    private static final String RAPID_API_KEY = BuildConfig.RAPID_API_KEY;
    private static final String RAPID_API_HOST = "spotify23.p.rapidapi.com";

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

        songAdapter = new SongAdapter(
                songResults,
                (song, list, position) -> {
                    if (getIntent().getBooleanExtra("IS_SELECTION_MODE", false)) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("SELECTED_SONG", song);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Intent intent = new Intent(SearchResultsActivity.this, MusicPlayerActivity.class);
                        intent.putExtra("SONG_KEY", song);
                        intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
                        intent.putExtra("CURRENT_INDEX", position);
                        startActivity(intent);
                    }
                },
                (song, position) -> {
                    showAlbumOptionsDialog(song);
                }
        );

        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerResults.setAdapter(songAdapter);

        String query = getIntent().getStringExtra("EXTRA_QUERY");
        String filterType = getIntent().getStringExtra("EXTRA_FILTER");

        if (query != null) {
            txtSearchTitle.setText("Resultados para: \"" + query + "\"");
            performSearch(query, filterType);
        }
    }

    private void showAlbumOptionsDialog(Song selectedSong) {
        CharSequence[] options = new CharSequence[]{
                "Entrar no Álbum",
                "Adicionar aos Downloaded Playlists"
        };

        new AlertDialog.Builder(this)
                .setTitle("Opções do Álbum")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(SearchResultsActivity.this, PlaylistActivity.class);
                        intent.putExtra("ALBUM_TITLE", selectedSong.getArtist());
                        startActivity(intent);

                    } else if (which == 1) {
                        saveAlbumToDownloads(selectedSong);
                    }
                })
                .show();
    }

    private void saveAlbumToDownloads(Song song) {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String userEmail = prefsManager.getUserEmail();

        List<Song> albumSongs = new ArrayList<>();
        for (Song s : songResults) {
            if (s.getImageUrl() != null && s.getImageUrl().equals(song.getImageUrl())) {
                albumSongs.add(s);
            }
        }
        if (albumSongs.isEmpty()) {
            albumSongs.add(song);
        }

        Playlist downloadedAlbum = new Playlist(
                "album_" + System.currentTimeMillis(),
                "Álbum de " + song.getArtist(),
                song.getImageUrl(),
                true
        );

        downloadedAlbum.setSongs(albumSongs);

        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists(userEmail);
        if (downloadedPlaylists == null) {
            downloadedPlaylists = new ArrayList<>();
        }

        downloadedPlaylists.add(downloadedAlbum);
        prefsManager.saveDownloadedPlaylists(userEmail, downloadedPlaylists);

        Toast.makeText(this, "Álbum guardado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void performSearch(String query, String filterType) {
        songResults = new ArrayList<>();
        if (query == null) query = "";
        final String searchQuery = query.toLowerCase().trim();

        if (filterType == null || filterType.isEmpty()) {
            filterType = "all";
        }

        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String userEmail = prefsManager.getUserEmail();

        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists(userEmail);
        List<Playlist> myPlaylists = prefsManager.loadMyPlaylists(userEmail);

        if (filterType.equals("all") || filterType.equals("playlists") || filterType.equals("multi")) {
            List<Playlist> allPlaylists = new ArrayList<>();
            if (downloadedPlaylists != null) allPlaylists.addAll(downloadedPlaylists);
            if (myPlaylists != null) allPlaylists.addAll(myPlaylists);

            for (Playlist playlist : allPlaylists) {
                if (playlist != null && playlist.getTitle() != null) {
                    String playlistTitle = playlist.getTitle().toLowerCase();
                    if (playlistTitle.contains(searchQuery)) {
                        if (playlist.getSongs() != null) {
                            for (Song song : playlist.getSongs()) {
                                if (song != null && !songResults.contains(song)) {
                                    songResults.add(song);
                                }
                            }
                        }
                    }
                }
            }
        }

        SpotifyAPIService apiService = RetrofitClient.getClient().create(SpotifyAPIService.class);

        Call<SpotifySearchResponse> call = apiService.searchSpotify(RAPID_API_KEY, RAPID_API_HOST, searchQuery, "albums");

        call.enqueue(new Callback<SpotifySearchResponse>() {
            @Override
            public void onResponse(Call<SpotifySearchResponse> call, Response<SpotifySearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SpotifySearchResponse searchResponse = response.body();

                    if (searchResponse.getAlbums() != null && searchResponse.getAlbums().getItems() != null) {
                        for (SpotifySearchResponse.AlbumEdge edge : searchResponse.getAlbums().getItems()) {
                            SpotifySearchResponse.AlbumData album = edge.getData();
                            if (album != null) {
                                String coverUrl = "";
                                if (album.getCoverArt() != null && album.getCoverArt().getSources() != null && !album.getCoverArt().getSources().isEmpty()) {
                                    coverUrl = album.getCoverArt().getSources().get(0).getUrl();
                                }

                                String artistName = "Desconhecido";
                                if (album.getArtists() != null && album.getArtists().getItems() != null && !album.getArtists().getItems().isEmpty()) {
                                    if (album.getArtists().getItems().get(0).getProfile() != null) {
                                        artistName = album.getArtists().getItems().get(0).getProfile().getName();
                                    }
                                }

                                String albumId = album.getUri() != null ? album.getUri() : System.currentTimeMillis() + "";
                                Song song = new Song(
                                        albumId,
                                        album.getName(),
                                        artistName,
                                        coverUrl
                                );

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
                    } else {
                        android.util.Log.d("API_SUCCESS", "Albums ou items veio a null!");
                    }
                }

                if (songResults.isEmpty()) {
                    Toast.makeText(SearchResultsActivity.this, "Nenhum resultado encontrado.", Toast.LENGTH_SHORT).show();
                }

                if (songAdapter != null) {
                    songAdapter.setSongs(songResults);
                }
            }

            @Override
            public void onFailure(Call<SpotifySearchResponse> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Falha no pedido à API: " + t.getMessage(), t);
                Toast.makeText(SearchResultsActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();

                if (songAdapter != null) {
                    songAdapter.setSongs(songResults);
                }
            }
        });
    }
}