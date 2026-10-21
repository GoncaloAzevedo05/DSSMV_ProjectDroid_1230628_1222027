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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

        // Suporte a EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização de Views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progressBar);

        // Submeter Registo
        btnRegister.setOnClickListener(v -> performRegister());

        // Voltar para o Ecrã de Login
        txtLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // 1. Validação de Nome de Utilizador
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Introduz o teu nome de utilizador");
            etUsername.requestFocus();
            return;
        }

        // 2. Validação de Email Vazio
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduz o teu e-mail");
            etEmail.requestFocus();
            return;
        }

        // 3. Validação do Formato do Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduz um e-mail válido (ex: utilizador@dominio.com)");
            etEmail.requestFocus();
            return;
        }

        // 4. Validação de Password Vazia
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduz uma palavra-passe");
            etPassword.requestFocus();
            return;
        }

        // 5. Validação de Comprimento da Password (entre 6 e 16 caracteres)
        if (password.length() < 6 || password.length() > 16) {
            etPassword.setError("A palavra-passe deve conter entre 6 e 16 caracteres");
            etPassword.requestFocus();
            return;
        }

        // 6. Validação de Confirmação da Password
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("As palavras-passe não coincidem");
            etConfirmPassword.requestFocus();
            return;
        }

        showLoading(true);

        // TODO: Substituir pela chamada do Supabase para registar utilizador
        // No Supabase Auth, se o e-mail já existir, a API devolverá um erro que podes capturar assim:
        /*
        supabase.auth.signUp(email, password, res -> {
            if (res.isSuccessful()) { ... }
            else { etEmail.setError("Este e-mail já está em uso por outra conta."); }
        });
        */

        // Simulação temporária de Registo:
        btnRegister.postDelayed(() -> {
            showLoading(false);
            Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

            // Navegar diretamente para a MainActivity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1200);
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
