package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.ProfileResponse;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String userEmail = "";
    private String userName = "";
    private String userCreatedAt = "";
    private SharedPreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefsManager = new SharedPreferencesManager(this);

        if (getIntent() != null) {
            userEmail = getIntent().getStringExtra("USER_EMAIL");
            userName = getIntent().getStringExtra("USER_NAME");
            userCreatedAt = getIntent().getStringExtra("USER_CREATED_AT");
        }

        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = prefsManager.getUserEmail();
        }

        fetchUserProfile();

        Button btnPlaylists = findViewById(R.id.btnPlaylists);
        Button btnPlayer = findViewById(R.id.btnPlayer);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnPlaylists.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
            startActivity(intent);
        });

        btnPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("USER_CREATED_AT", userCreatedAt);
            startActivity(intent);
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserProfile() {
        if (userEmail == null || userEmail.isEmpty()) return;

        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);

        api.getUserProfile("Bearer " + SupaBaseClient.getApiKey(), "eq." + userEmail)
                .enqueue(new Callback<List<ProfileResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProfileResponse>> call, Response<List<ProfileResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ProfileResponse profile = response.body().get(0);

                            if (profile.getUsername() != null && !profile.getUsername().isEmpty()) {
                                userName = profile.getUsername();
                            }
                            if (profile.getCreatedAt() != null && !profile.getCreatedAt().isEmpty()) {
                                userCreatedAt = profile.getCreatedAt();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProfileResponse>> call, Throwable t) {
                    }
                });
    }
}