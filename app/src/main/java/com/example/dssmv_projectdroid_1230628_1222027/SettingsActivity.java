package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.dssmv_projectdroid_1230628_1222027.Model.ProfileData;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;


public class SettingsActivity extends AppCompatActivity {

    private ImageView imgProfilePicture;
    private TextView txtUserName, txtUserEmail;
    private Button btnAccountInfo, btnFavoriteSongs, btnTopArtist, btnLogout, btnDownloadedMusic;
    private ProfileData currentProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnAccountInfo = findViewById(R.id.btnAccountInfo);
        btnFavoriteSongs = findViewById(R.id.btnFavoriteSongs);
        btnTopArtist = findViewById(R.id.btnTopArtist);
        btnLogout = findViewById(R.id.btnLogout);
        btnDownloadedMusic = findViewById(R.id.btnDownloadedMusic);

        String email = getIntent().getStringExtra("USER_EMAIL");
        String name = getIntent().getStringExtra("USER_NAME");
        String rawDate = getIntent().getStringExtra("USER_CREATED_AT");

        if (email == null || email.isEmpty()) {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String formattedDate = (rawDate != null && rawDate.length() >= 10) ? rawDate.substring(0, 10) : "N/A";
        String displayName = (name != null && !name.isEmpty()) ? name : email.split("@")[0];

        currentProfile = new ProfileData(displayName, email, null, formattedDate);

        txtUserName.setText(currentProfile.getName());
        txtUserEmail.setText(currentProfile.getEmail());

        btnAccountInfo.setOnClickListener(v -> {
            String accountDetails = "Nome: " + currentProfile.getName() + "\n" +
                    "Email: " + currentProfile.getEmail() + "\n" +
                    "Conta Criada: " + currentProfile.getCreatedAt();

            new AlertDialog.Builder(this)
                    .setTitle("Account Info")
                    .setMessage(accountDetails)
                    .setPositiveButton("OK", null)
                    .show();
                }
        );

        btnFavoriteSongs.setOnClickListener(v -> {
                    Intent intent = new Intent(SettingsActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                }
        );

        btnTopArtist.setOnClickListener(v -> {
                    Intent intent = new Intent(SettingsActivity.this, MostListenedActivity.class);
                    startActivity(intent);
                }
        );

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sair da Conta")
                    .setMessage("Tens a certeza que pretendes terminar a sessão?")
                    .setPositiveButton("Sair", (dialog, which) -> {
                        SharedPreferencesManager prefsManager = new SharedPreferencesManager(SettingsActivity.this);
                        prefsManager.clearUserSession();


                        Toast.makeText(SettingsActivity.this, "Sessão terminada", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnDownloadedMusic.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DownloadedSongsActivity.class);
            intent.putExtra("USER_EMAIL", currentProfile != null ? currentProfile.getEmail() : "");
            startActivity(intent);
        });
    }
}