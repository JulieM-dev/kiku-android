package com.mongault.kiku.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mongault.kiku.data.local.TokenManager;
import com.mongault.kiku.databinding.ActivityLoginBinding;
import com.mongault.kiku.ui.deckslist.DecksListActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getAuthSuccess().observe(this, authResponse -> {
            TokenManager.getInstance(this).saveToken(authResponse.getToken());
            navigateToDeckList();
        });

        viewModel.getError().observe(this, error ->
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show());

        viewModel.getIsLoading().observe(this, isLoading ->
                binding.buttonLogin.setEnabled(!isLoading));
    }

    private void setupButtons() {
        binding.buttonLogin.setOnClickListener(v -> {
            TokenManager.getInstance(this).clearToken();
            String email = binding.textInputEmail.getText().toString().trim();
            String password = binding.textInputPassword.getText().toString().trim();
            viewModel.login(email, password);
        });

        binding.buttonGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void navigateToDeckList() {
        Intent intent = new Intent(this, DecksListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}