package com.mongault.kiku.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ActivityRegisterBinding;
import com.mongault.kiku.ui.deckslist.DecksListActivity;

public class RegisterActivity extends AppCompatActivity {

    // ─── Fields ───────────────────────────────────────────
    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    // ─── Lifecycle ────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    // ─── Setup ────────────────────────────────────────────
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getAuthSuccess().observe(this, authResponse -> {
            TokenManager.getInstance(this).saveToken(authResponse.getToken());
            TokenManager.getInstance(this).saveUsername(authResponse.getUsername());
            navigateToDeckList();
        });

        viewModel.getError().observe(this, error ->
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show());

        viewModel.getIsLoading().observe(this, isLoading ->
                binding.buttonRegister.setEnabled(!isLoading));
    }

    private void setupButtons() {
        binding.buttonRegister.setOnClickListener(v -> {
            TokenManager.getInstance(this).clearToken();
            String username = binding.textInputUsername.getText().toString();
            String email = binding.textInputEmail.getText().toString().trim();
            String password = binding.textInputPassword.getText().toString().trim();
            String confirm = binding.textInputConfirmPassword.getText().toString().trim();
            viewModel.register(username, email, password, confirm);
        });

        binding.buttonGoToLogin.setOnClickListener(v -> finish());
    }

    // ─── Navigation ───────────────────────────────────────
    private void navigateToDeckList() {
        Intent intent = new Intent(this, DecksListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}