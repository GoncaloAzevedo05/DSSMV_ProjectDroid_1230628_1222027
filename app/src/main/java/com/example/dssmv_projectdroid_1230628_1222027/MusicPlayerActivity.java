package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.PlaylistReq;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserFavoriteReq;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Playlist;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {
    private ImageView imgAlbumCover;
    private TextView txtSongTitle, txtArtistName, txtCurrentTime, txtTotalTime;
    private ImageButton btnFavorite, btnPrevious, btnPlayPause, btnNext, btnAddToPlaylist;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isFavorite = false;

    private List<Song> songList;
    private int currentIndex = 0;
    private Song currentSong;
    private String audioUrl = "";

    private SharedPreferencesManager prefsManager;

    private final Handler handler = new Handler();
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                txtCurrentTime.setText(formatTime(currentPosition));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_player), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);

        imgAlbumCover = findViewById(R.id.imgAlbumCover);
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtArtistName = findViewById(R.id.txtArtistName);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnAddToPlaylist = findViewById(R.id.btnAddToPlaylist);
        seekBar = findViewById(R.id.seekBar);

        songList = (List<Song>) getIntent().getSerializableExtra("SONG_LIST");
        currentIndex = getIntent().getIntExtra("CURRENT_INDEX", 0);
        currentSong = (Song) getIntent().getSerializableExtra("SONG_KEY");

        if (songList == null || songList.isEmpty()) {
            songList = new ArrayList<>();
            if (currentSong != null) {
                songList.add(currentSong);
            } else {
                currentSong = new Song("1", "Starboy", "The Weeknd", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
                songList.add(currentSong);
            }
        } else if (currentSong == null && !songList.isEmpty()) {
            currentSong = songList.get(currentIndex);
        }

        loadSongData(currentSong);

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    handler.removeCallbacks(updateSeekBar);
                } else {
                    mediaPlayer.start();
                    isPlaying = true;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    handler.post(updateSeekBar);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    txtCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && isPlaying) {
                    handler.post(updateSeekBar);
                }
            }
        });

        btnFavorite.setOnClickListener(v -> {
            if (currentSong != null) {
                toggleFavoriteStatus(currentSong);
                if (isFavorite) {
                    btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    btnFavorite.setImageResource(android.R.drawable.btn_star);
                    Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddToPlaylist.setOnClickListener(v -> {
            if (currentSong != null) {
                showAddPlaylistDialog(currentSong);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (songList != null && !songList.isEmpty()) {
                currentIndex = (currentIndex + 1) % songList.size();
                currentSong = songList.get(currentIndex);
                loadSongData(currentSong);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (songList != null && !songList.isEmpty()) {
                currentIndex = (currentIndex - 1 + songList.size()) % songList.size();
                currentSong = songList.get(currentIndex);
                loadSongData(currentSong);
            }
        });
    }

    private void loadSongData(Song song) {
        if (song == null) return;

        txtSongTitle.setText(song.getTitle());
        txtArtistName.setText(song.getArtist());
        audioUrl = song.getAudioUrl();

        if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(song.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgAlbumCover);
        } else {
            imgAlbumCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        checkIfFavorite(song);
        playAudio();
    }

    private void playAudio() {
        if (audioUrl == null || audioUrl.isEmpty()) {
            Toast.makeText(this, "Link de áudio indisponível", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            if (audioUrl.startsWith("content://")) {
                mediaPlayer.setDataSource(this, Uri.parse(audioUrl));
            } else {
                mediaPlayer.setDataSource(audioUrl);
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);

                int duration = mediaPlayer.getDuration();
                seekBar.setMax(duration);
                txtTotalTime.setText(formatTime(duration));

                handler.removeCallbacks(updateSeekBar);
                handler.post(updateSeekBar);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (songList != null && !songList.isEmpty()) {
                    currentIndex = (currentIndex + 1) % songList.size();
                    currentSong = songList.get(currentIndex);
                    loadSongData(currentSong);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao carregar o leitor de áudio", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void checkIfFavorite(Song song) {
        String userEmail = prefsManager.getUserEmail();
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        List<Song> favoriteSongs = prefsManager.loadFavoriteSongs(userEmail);

        isFavorite = false;
        if (favoriteSongs != null) {
            for (Song s : favoriteSongs) {
                if (s.getId().equals(song.getId())) {
                    isFavorite = true;
                    break;
                }
            }
        }

        if (isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star);
        }
    }

    private void toggleFavoriteStatus(Song song) {
        String userEmail = prefsManager.getUserEmail();
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        List<Song> favoriteSongs = prefsManager.loadFavoriteSongs(userEmail);
        if (favoriteSongs == null) {
            favoriteSongs = new ArrayList<>();
        }

        boolean found = false;
        for (int i = 0; i < favoriteSongs.size(); i++) {
            if (favoriteSongs.get(i).getId().equals(song.getId())) {
                favoriteSongs.remove(i);
                found = true;
                break;
            }
        }

        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);

        if (!found) {
            favoriteSongs.add(song);
            isFavorite = true;
            UserFavoriteReq favoriteReq = new UserFavoriteReq(
                    userEmail,
                    song.getId(),
                    song.getTitle(),
                    song.getArtist(),
                    song.getAudioUrl(),
                    song.getImageUrl()
            );

            api.addFavorite("Bearer " + SupaBaseClient.getApiKey(), favoriteReq).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        } else {
            isFavorite = false;
            api.removeFavorite("Bearer " + SupaBaseClient.getApiKey(), "eq." + userEmail, "eq." + song.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        }

        prefsManager.saveFavoriteSongs(userEmail, favoriteSongs);
    }

    private void showAddPlaylistDialog(Song song) {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String userEmail = prefsManager.getUserEmail();
        List<Playlist> myPlaylists = prefsManager.loadMyPlaylists(userEmail);

        if (myPlaylists == null || myPlaylists.isEmpty()) {
            Toast.makeText(this, "Ainda não tens playlists criadas.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] playlistNames = new String[myPlaylists.size()];
        for (int i = 0; i < myPlaylists.size(); i++) {
            playlistNames[i] = myPlaylists.get(i).getTitle();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicionar à Playlist");
        builder.setItems(playlistNames, (dialog, which) -> {
            Playlist selectedPlaylist = myPlaylists.get(which);
            if (selectedPlaylist.getSongs() == null) {
                selectedPlaylist.setSongs(new ArrayList<>());
            }
            boolean exists = false;
            for (Song s : selectedPlaylist.getSongs()) {
                if (s.getId().equals(song.getId())) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                selectedPlaylist.getSongs().add(song);
                prefsManager.saveMyPlaylists(userEmail, myPlaylists);

                SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);
                PlaylistReq playlistReq = new PlaylistReq(
                        userEmail,
                        selectedPlaylist.getTitle(),
                        selectedPlaylist.getImageUrl(),
                        selectedPlaylist.isDownloaded(),
                        selectedPlaylist.getSongs()
                );

                api.syncPlaylist("Bearer " + SupaBaseClient.getApiKey(), playlistReq)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MusicPlayerActivity.this, "Música sincronizada no Supabase!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {}
                        });
                Toast.makeText(this, "Adicionada a " + selectedPlaylist.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "A música já faz parte desta playlist.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}