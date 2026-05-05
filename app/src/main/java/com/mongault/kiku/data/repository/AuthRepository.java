package com.mongault.kiku.data.repository;

import android.content.Context;

import com.mongault.kiku.data.remote.ApiClient;
import com.mongault.kiku.data.remote.ApiService;
import com.mongault.kiku.model.AuthRequest;
import com.mongault.kiku.model.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getInstance(context);
    }

    public void login(String email, String password, RepositoryCallback<AuthResponse> callback) {
        AuthRequest request = new AuthRequest(email, password, null);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Invalid credentials");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(String username, String email, String password, RepositoryCallback<AuthResponse> callback) {
        AuthRequest request = new AuthRequest(email, password, username);
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}