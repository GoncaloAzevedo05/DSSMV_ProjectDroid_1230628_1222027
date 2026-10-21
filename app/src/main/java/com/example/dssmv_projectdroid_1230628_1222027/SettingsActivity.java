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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dssmv_projectdroid_1230628_1222027.Model.ProfileData;

public class SettingsActivity extends AppCompatActivity {

    private ImageView imgProfilePicture;
    private TextView txtUserName, txtUserEmail;
    private Button btnAccountInfo, btnFavoriteSongs, btnTopArtist, btnLogout, btnDownloadedMusic;

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

        ProfileData userProfile = new ProfileData(
                "Teste",
                "Teste@teste.com",
                "https://media.printler.com/media/photo/170754.jpg"
        );

        txtUserName.setText(userProfile.getName());
        txtUserEmail.setText(userProfile.getEmail());


    Glide.with(this)
            .load(userProfile.getProfilePictureUrl())
            .apply(RequestOptions.circleCropTransform())
            .placeholder(android.R.drawable.ic_menu_camera)
                .into(imgProfilePicture);

        btnAccountInfo.setOnClickListener(v -> {
            String accountDetails = "Nome: " + userProfile.getName() + "\n" +
                    "Email: " + userProfile.getEmail() + "\n" +
                    "Data de Nascimento: 17/11/2005\n" +
                    "Conta Criada: Outubro 2026";

            new AlertDialog.Builder(this)
                    .setTitle("Account Info")
                    .setMessage(accountDetails)
                    .setPositiveButton("OK", null)
                    .show();
        }
        );

        btnFavoriteSongs.setOnClickListener(v ->{
            Intent intent = new Intent(SettingsActivity.this, FavoritesActivity.class);
            startActivity(intent);
        }
        );

        btnTopArtist.setOnClickListener(v ->{
            Intent intent = new Intent(SettingsActivity.this, MostListenedActivity.class);
            startActivity(intent);
        }
        );

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sair da Conta")
                    .setMessage("Tens a certeza que pretendes terminar a sessão?")
                    .setPositiveButton("Sair", (dialog, which) -> {
                        // TODO: Limpar sessão no Supabase Auth quando estiver integrado
                        // SupabaseClient.auth.signOut();

                        Toast.makeText(SettingsActivity.this, "Sessão terminada", Toast.LENGTH_SHORT).show();

                        // Redirecionar para o LoginActivity limpando toda a pilha de Atividades
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
            startActivity(intent);
        });
    }
}