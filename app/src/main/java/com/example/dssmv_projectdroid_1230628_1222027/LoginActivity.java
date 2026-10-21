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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView txtRegister, txtForgotPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Suporte a EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização de Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        // Listener do Botão Login
        btnLogin.setOnClickListener(v -> performLogin());

        // Listener para Criar Nova Conta / Registo
        txtRegister.setOnClickListener(v -> {
             Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
             startActivity(intent);
            Toast.makeText(this, "Navegar para ecrã de registo", Toast.LENGTH_SHORT).show();
        });

//        txtRegister.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            startActivity(intent);
//        });

        // Listener para Recuperar Palavra-passe
        txtForgotPassword.setOnClickListener(v -> performPasswordReset());
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // 1. Validação de Email Vazio
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Por favor, introduz o e-mail");
            etEmail.requestFocus();
            return;
        }

        // 2. Validação da Estrutura/Formato do Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduz um endereço de e-mail válido (ex: utilizador@dominio.com)");
            etEmail.requestFocus();
            return;
        }

        // 3. Validação de Password Vazia
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Por favor, introduz a palavra-passe");
            etPassword.requestFocus();
            return;
        }

        // 4. Validação de Comprimento da Password (entre 6 e 16 caracteres)
        if (password.length() < 6 || password.length() > 16) {
            etPassword.setError("A palavra-passe deve conter entre 6 e 16 caracteres");
            etPassword.requestFocus();
            return;
        }

        showLoading(true);

        // TODO: Substituir por chamada real ao Supabase Auth
        // Exemplo: client.auth.signInWithPassword(email, password)
        btnLogin.postDelayed(() -> {
            showLoading(false);

            // Simulação de Sucesso no Login
            Toast.makeText(LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();

            // Abrir a MainActivity e encerrar a LoginActivity para não voltar ao pressionar "Back"
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1200);
    }

    private void performPasswordReset() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        // Validar se o utilizador preencheu o e-mail antes de pedir o envio de recuperação
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Preenche um e-mail válido para receber a hiperligação de recuperação");
            etEmail.requestFocus();
            return;
        }

        showLoading(true);

        // TODO: Substituir por chamada real do Supabase:
        // client.auth.resetPasswordForEmail(email)
        btnLogin.postDelayed(() -> {
            showLoading(false);
            Toast.makeText(LoginActivity.this, "E-mail de recuperação enviado para " + email, Toast.LENGTH_LONG).show();
        }, 1000);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }
    }
}