package com.example.dssmv_projectdroid_1230628_1222027;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dssmv_projectdroid_1230628_1222027.ApiData.AuthResponse;
import com.example.dssmv_projectdroid_1230628_1222027.ApiData.UserCred;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseClient;
import com.example.dssmv_projectdroid_1230628_1222027.Retrofit.SupaBaseInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView txtLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> performRegister());

        txtLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Introduz o teu nome de utilizador");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduz o teu e-mail");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduz um e-mail válido (ex: utilizador@dominio.com)");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduz uma palavra-passe");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            etPassword.setError("A palavra-passe deve conter entre 6 e 16 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("As palavras-passe não coincidem");
            etConfirmPassword.requestFocus();
            return;
        }

        showLoading(true);

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("display_name", username);
        data.put("full_name", username);

        SupaBaseInterface api = SupaBaseClient.getClient().create(SupaBaseInterface.class);
        UserCred credentials = new UserCred(email, password, data);

        api.register(credentials).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    String userEmail = email;
                    String userName = username;
                    String userCreatedAt = "";

                    if (auth.getUser() != null && auth.getUser().getCreatedAt() != null && !auth.getUser().getCreatedAt().isEmpty()) {
                        userCreatedAt = auth.getUser().getCreatedAt();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD", Locale.getDefault());
                        userCreatedAt = sdf.format(new Date());
                    }

                    SharedPreferencesManager prefsManager = new SharedPreferencesManager(RegisterActivity.this);
                    prefsManager.saveUserSession(userEmail, userName, userCreatedAt);

                    Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    intent.putExtra("USER_NAME", userName);
                    intent.putExtra("USER_CREATED_AT", userCreatedAt);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    etEmail.setError("Este e-mail já se encontra registado.");
                    etEmail.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "Erro de ligação: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }
    }
}