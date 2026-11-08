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
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.JamendoPlaylistsResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.JamendoResponse;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.JamendoAPIService;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.RetrofitClient;

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

    private static final String JAMENDO_CLIENT_ID = BuildConfig.JAMENDO_CLIENT_ID;
    private static final String PLAYLIST_PREFIX = "jamendo_playlist_";

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
                    if (isJamendoPlaylist(song)) {
                        fetchPlaylistTracks(song, false);
                        return;
                    }
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
                    if (isJamendoPlaylist(song)) {
                        fetchPlaylistTracks(song, true);
                    } else {
                        showAlbumOptionsDialog(song);
                    }
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

    private boolean isJamendoPlaylist(Song song) {
        return song != null && song.getId() != null && song.getId().startsWith(PLAYLIST_PREFIX);
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
        final String rawQuery = query.trim();
        final String searchQuery = rawQuery.toLowerCase();

        if (filterType == null || filterType.isEmpty()) {
            filterType = "multi";
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
                if (playlist != null && playlist.getTitle() != null
                        && playlist.getTitle().toLowerCase().contains(searchQuery)
                        && playlist.getSongs() != null) {
                    for (Song song : playlist.getSongs()) {
                        if (song != null && !songResults.contains(song)) {
                            songResults.add(song);
                        }
                    }
                }
            }
        }

        if (filterType.equals("playlists")) {
            searchJamendoPlaylists(rawQuery);
            return;
        }

        searchJamendo(rawQuery, filterType);
    }

    private void searchJamendo(final String query, final String filterType) {
        JamendoAPIService api = RetrofitClient.getClient().create(JamendoAPIService.class);

        String trackName = null, search = null, artistName = null, albumName = null;
        switch (filterType) {
            case "tracks":  trackName = query;  break;
            case "albums":  albumName = query;  break;
            case "artists": artistName = query; break;
            default:        search = query;     break;
        }

        api.searchTracks(JAMENDO_CLIENT_ID, "json", 30, 300, trackName, search, artistName, albumName)
                .enqueue(new Callback<JamendoResponse>() {
                    @Override
                    public void onResponse(Call<JamendoResponse> call, Response<JamendoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JamendoResponse body = response.body();

                            if (body.getHeaders() != null && body.getHeaders().getCode() != 0) {
                                String msg = body.getHeaders().getErrorMessage();
                                android.util.Log.e("JAMENDO_ERR", "code=" + body.getHeaders().getCode() + " " + msg);
                                Toast.makeText(SearchResultsActivity.this, "Erro Jamendo: " + msg, Toast.LENGTH_LONG).show();
                            } else if (body.getResults() != null) {
                                for (JamendoResponse.JamendoTrack t : body.getResults()) {
                                    if (t.getAudio() == null || t.getAudio().isEmpty()) continue;

                                    boolean exists = false;
                                    for (Song s : songResults) {
                                        if (t.getId().equals(s.getId())) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (exists) continue;

                                    songResults.add(new Song(
                                            t.getId(),
                                            t.getName(),
                                            t.getArtistName(),
                                            t.getAudio(),
                                            t.getImage()));
                                }
                            }
                        } else {
                            Toast.makeText(SearchResultsActivity.this,
                                    "Erro HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                        }

                        if (filterType.equals("multi")) {
                            searchJamendoPlaylists(query);
                        } else {
                            finishSearch();
                        }
                    }

                    @Override
                    public void onFailure(Call<JamendoResponse> call, Throwable t) {
                        android.util.Log.e("API_ERROR", "Falha no pedido à API: " + t.getMessage(), t);
                        Toast.makeText(SearchResultsActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        finishSearch();
                    }
                });
    }

    private void searchJamendoPlaylists(final String query) {
        JamendoAPIService api = RetrofitClient.getClient().create(JamendoAPIService.class);

        api.searchPlaylists(JAMENDO_CLIENT_ID, "json", 20, query)
                .enqueue(new Callback<JamendoPlaylistsResponse>() {
                    @Override
                    public void onResponse(Call<JamendoPlaylistsResponse> call, Response<JamendoPlaylistsResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResults() != null) {
                            for (JamendoPlaylistsResponse.JamendoPlaylist p : response.body().getResults()) {
                                String author = p.getUserName() != null ? p.getUserName() : "Jamendo";
                                songResults.add(new Song(
                                        PLAYLIST_PREFIX + p.getId(),
                                        p.getName(),
                                        "Playlist de " + author,
                                        "",
                                        ""));
                            }
                        }
                        finishSearch();
                    }

                    @Override
                    public void onFailure(Call<JamendoPlaylistsResponse> call, Throwable t) {
                        android.util.Log.e("API_ERROR", "Falha nas playlists: " + t.getMessage(), t);
                        finishSearch();
                    }
                });
    }

    private void finishSearch() {
        if (songResults.isEmpty()) {
            Toast.makeText(this, "Nenhum resultado encontrado.", Toast.LENGTH_SHORT).show();
        }
        songAdapter.setSongs(songResults);
    }

    private void fetchPlaylistTracks(final Song playlistSong, final boolean saveToDownloads) {
        String playlistId = playlistSong.getId().substring(PLAYLIST_PREFIX.length());
        JamendoAPIService api = RetrofitClient.getClient().create(JamendoAPIService.class);

        api.getPlaylistTracks(JAMENDO_CLIENT_ID, "json", playlistId)
                .enqueue(new Callback<JamendoPlaylistsResponse>() {
                    @Override
                    public void onResponse(Call<JamendoPlaylistsResponse> call, Response<JamendoPlaylistsResponse> response) {
                        List<Song> tracks = new ArrayList<>();

                        if (response.isSuccessful() && response.body() != null
                                && response.body().getResults() != null
                                && !response.body().getResults().isEmpty()) {
                            JamendoPlaylistsResponse.JamendoPlaylist p = response.body().getResults().get(0);
                            if (p.getTracks() != null) {
                                for (JamendoResponse.JamendoTrack t : p.getTracks()) {
                                    if (t.getAudio() == null || t.getAudio().isEmpty()) continue;
                                    tracks.add(new Song(
                                            t.getId(),
                                            t.getName(),
                                            t.getArtistName(),
                                            t.getAudio(),
                                            t.getImage()));
                                }
                            }
                        }

                        if (tracks.isEmpty()) {
                            Toast.makeText(SearchResultsActivity.this,
                                    "Esta playlist não tem músicas disponíveis.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (saveToDownloads) {
                            saveJamendoPlaylist(playlistSong, tracks);
                        } else {
                            songResults = tracks;
                            txtSearchTitle.setText("Playlist: " + playlistSong.getTitle());
                            songAdapter.setSongs(songResults);
                        }
                    }

                    @Override
                    public void onFailure(Call<JamendoPlaylistsResponse> call, Throwable t) {
                        android.util.Log.e("API_ERROR", "Falha ao obter playlist: " + t.getMessage(), t);
                        Toast.makeText(SearchResultsActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveJamendoPlaylist(Song playlistSong, List<Song> tracks) {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String userEmail = prefsManager.getUserEmail();

        Playlist playlist = new Playlist(
                "jamendo_" + System.currentTimeMillis(),
                playlistSong.getTitle(),
                tracks.get(0).getImageUrl(),
                true
        );
        playlist.setSongs(tracks);

        List<Playlist> downloadedPlaylists = prefsManager.loadDownloadedPlaylists(userEmail);
        if (downloadedPlaylists == null) {
            downloadedPlaylists = new ArrayList<>();
        }
        downloadedPlaylists.add(playlist);
        prefsManager.saveDownloadedPlaylists(userEmail, downloadedPlaylists);

        Toast.makeText(this, "Playlist guardada com sucesso!", Toast.LENGTH_SHORT).show();
    }
}