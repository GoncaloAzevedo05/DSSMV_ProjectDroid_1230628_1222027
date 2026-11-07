package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserFavoriteResponse;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.MusicPlayerActivity;
import com.example.dssmv_projectdroid_1230628_1222027.R;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerFavorites;
    private SharedPreferencesManager prefsManager;
    private SongAdapter adapter;
    private List<Song> favoriteSongsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        if (recyclerFavorites != null) {
            recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        fetchRemoteFavorites();
    }

    private void loadFavorites() {
        String userEmail = prefsManager.getUserEmail();
        List<Song> localSongs = prefsManager.loadFavoriteSongs(userEmail);

        favoriteSongsList.clear();
        if (localSongs != null && !localSongs.isEmpty()) {
            favoriteSongsList.addAll(localSongs);
        }

        SongAdapter adapter = new SongAdapter(favoriteSongsList, (song, list, position) -> {
            Intent intent = new Intent(FavoritesActivity.this, MusicPlayerActivity.class);
            intent.putExtra("SONG_KEY", song);
            intent.putExtra("SONG_LIST", (ArrayList<Song>) list);
            intent.putExtra("CURRENT_INDEX", position);
            startActivity(intent);
        });

        if (recyclerFavorites != null) {
            recyclerFavorites.setAdapter(adapter);
        }
    }

    private void fetchRemoteFavorites() {
        String userEmail = prefsManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) return;
        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);

        api.getUserFavorites(SupaBaseClient.getApiKey(), "eq." + userEmail)
                .enqueue(new Callback<List<UserFavoriteResponse>>() {
                    @Override
                    public void onResponse(Call<List<UserFavoriteResponse>> call, Response<List<UserFavoriteResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<Song> remoteFavorites = new ArrayList<>();
                            for (UserFavoriteResponse item : response.body()) {
                                String id = item.getSongId() != null ? item.getSongId() : "";
                                String title = item.getSongTitle() != null ? item.getSongTitle() : "";
                                String artist = item.getArtistName() != null ? item.getArtistName() : "";
                                String audio = item.getAudioUrl() != null ? item.getAudioUrl() : "";
                                String image = item.getImageUrl() != null ? item.getImageUrl() : "";

                                Song s = new Song(id, title, artist, audio, image);
                                remoteFavorites.add(s);
                            }
                            prefsManager.saveFavoriteSongs(userEmail, remoteFavorites);
                            favoriteSongsList.clear();
                            favoriteSongsList.addAll(remoteFavorites);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserFavoriteResponse>> call, Throwable t) {}
                });
    }
}