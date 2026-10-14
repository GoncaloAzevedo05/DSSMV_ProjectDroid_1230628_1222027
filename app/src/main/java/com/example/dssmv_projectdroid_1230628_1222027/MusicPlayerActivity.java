package com.example.dssmv_projectdroid_1230628_1222027;

import android.os.Bundle;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;

public class MusicPlayerActivity extends AppCompatActivity {
    private ImageView imgAlbumCover;
    private TextView txtSongTitle, txtArtistName;
    private ImageButton btnFavorite, btnPrevious, btnPlayPause, btnNext;

    private boolean isPlaying = false;
    private boolean isFavorite = false;

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

        imgAlbumCover = findViewById(R.id.imgAlbumCover);
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtArtistName = findViewById(R.id.txtArtistName);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);

        Song currentSong = (Song) getIntent().getSerializableExtra("SONG_KEY");
        if (currentSong == null) {
            currentSong = new Song("Starboy", "The Weeknd", "", "https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b5d718688e");
        }

        txtSongTitle.setText(currentSong.getTitle());
        txtArtistName.setText(currentSong.getArtist());

        if (currentSong.getImageUrl() != null && !currentSong.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentSong.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgAlbumCover);
        }

        btnPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            if (isPlaying) {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                Toast.makeText(this, "funciona1", Toast.LENGTH_SHORT).show();
            } else {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                Toast.makeText(this, "funciona2", Toast.LENGTH_SHORT).show();
            }
        });

        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) {
                btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                Toast.makeText(this, "funciona3", Toast.LENGTH_SHORT).show();
            } else {
                btnFavorite.setImageResource(android.R.drawable.btn_star);
                Toast.makeText(this, "funciona4", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevious.setOnClickListener(v ->
                Toast.makeText(this, "funciona5", Toast.LENGTH_SHORT).show()
        );

        btnNext.setOnClickListener(v ->
                Toast.makeText(this, "funciona6", Toast.LENGTH_SHORT).show()
        );
    }
}